package com.amonga.snifflicks.core.compose.viewmodel

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amonga.snifflicks.BuildConfig
import com.amonga.snifflicks.core.compose.interfaces.IEvent
import com.amonga.snifflicks.core.compose.interfaces.ISideEffect
import com.amonga.snifflicks.core.compose.interfaces.IViewResult
import com.amonga.snifflicks.core.compose.interfaces.IViewState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by Aditya on 21/05/25.
 */

// Define a generic ViewModel class that implements the MVI pattern (Model-View-Intent).
// This class takes in four generic types: Event, Result, State, and Effect.
abstract class BaseViewModel<Event_: IEvent, Result: IViewResult, State: IViewState>(
	initialState: State // The initial state of the ViewModel.
): ViewModel() {

	private val baseViewModelErrorHandler = CoroutineExceptionHandler { _, throwable ->
		Timber.e(throwable)
	}

	open fun getErrorHandler(): CoroutineExceptionHandler = baseViewModelErrorHandler

	// Expose a StateFlow to observe the current state of the ViewModel.
	private val _states: MutableStateFlow<State> = MutableStateFlow(initialState)
	val states: StateFlow<State> = _states.asStateFlow()

	// Expose a Flow to observe any side effects produced by the ViewModel.
	private val _effects = Channel<ISideEffect>(capacity = Channel.BUFFERED)
	val effects: Flow<ISideEffect> = _effects.receiveAsFlow().shareIn(viewModelScope, SharingStarted.WhileSubscribed())

	// To receive events from the View layer.
	private val events = MutableSharedFlow<Event_>()

	@VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
	val jobIdle = AtomicBoolean(true)

	// The init block is run when an instance of this ViewModel is created.
	init {
		viewModelScope.launch(getErrorHandler()) {
			events.collect { event ->
				if (BuildConfig.DEBUG) {
					jobIdle.set(false)
				}

				withHandleScope { scope ->
					try {
						scope.handleEvent(event)
					} catch (t: Throwable) {
						onHandleEventException(event, t)
					}
				}

				if (BuildConfig.DEBUG) {
					jobIdle.set(true)
				}
			}
		}
	}

	open fun onHandleEventException(event: IEvent, t: Throwable) {
		throw t
	}

	// Define a function to receive incoming events from the View layer.
	fun processEvent(event: Event_) {
		viewModelScope.launch {
			//			Todo:: build better way for logging. Since currently minify is disabled simpleName will work.
			Timber.i("processEvent ${event.javaClass.simpleName} for ${this@BaseViewModel.javaClass.simpleName}")
			// Emit the incoming event to the SharedFlow.
			events.emit(event)
		}
	}

	protected abstract suspend fun HandleEventScope.handleEvent(event: Event_)

	protected abstract fun Result.reduce(oldState: State): State

	suspend fun ISideEffect.emit() {
		_effects.send(this)
	}

	private suspend fun withHandleScope(block: suspend (HandleEventScope) -> Unit) {
		HandleEventScopeImp().apply {
			block(this)
			complete()
		}
	}

	private inner class HandleEventScopeImp(private var isActive: Boolean = true): HandleEventScope() {

		override suspend fun Result.reduceToState() {

			if (isActive) {
				_states.emit(reduce(_states.value))
			} else {
				Timber.e(Throwable("${this::class.java} tried to reduce after scope completed"))
			}
		}

		fun complete() {
			isActive = false
		}
	}

	abstract inner class HandleEventScope {

		abstract suspend fun Result.reduceToState()
	}
}
