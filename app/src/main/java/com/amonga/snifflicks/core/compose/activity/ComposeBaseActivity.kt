package com.amonga.snifflicks.core.compose.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.amonga.snifflicks.core.compose.interfaces.ISideEffect
import com.amonga.snifflicks.core.compose.interfaces.IViewState
import com.amonga.snifflicks.core.compose.interfaces.MviFragmentActivityInterface
import com.amonga.snifflicks.core.compose.viewmodel.BaseViewModel
import kotlinx.coroutines.launch
import com.amonga.snifflicks.R

/**
 * Created by Aditya on 21/05/25.
 */

abstract class ComposeBaseActivity<VS : IViewState> : AppCompatActivity(),
    MviFragmentActivityInterface {

    abstract override fun getVMInstance(): BaseViewModel<*, *, VS>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupFlowListener()
        setContentView(R.layout.main_activity)
        setContentUI()
    }

    open fun setContentUI() {
        findViewById<ComposeView>(R.id.cv_container)?.let {
            it.setContent {
                ActivityStateContent()
            }
        }
    }

    @Composable
    abstract fun ActivityContent(initialState: State<VS>)

    abstract override fun onSideEffectReceived(sideEffect: ISideEffect)

    @Composable
    protected fun ActivityStateContent() {
        val vm = getVMInstance()
        val initialState = vm.states.collectAsStateWithLifecycle()
        ActivityContent(initialState)
    }

    private fun setupFlowListener() {
        val vm = getVMInstance()
        lifecycleScope.launch {
            vm.effects.collect {
                onSideEffectReceived(it)
            }
        }
    }
}
