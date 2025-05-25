package com.amonga.snifflicks.core.compose.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.amonga.snifflicks.core.compose.interfaces.ISideEffect
import com.amonga.snifflicks.core.compose.interfaces.IViewState
import com.amonga.snifflicks.core.compose.interfaces.MviFragmentActivityInterface
import com.amonga.snifflicks.core.compose.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

/**
 * Created by Aditya on 21/05/25.
 */

abstract class ComposeBaseFragment<VS : IViewState> : Fragment() {

    abstract fun getVmInstance(): BaseViewModel<*, *, VS>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (requireActivity() !is MviFragmentActivityInterface) {
            throw ClassCastException("${requireActivity().componentName} should implement ${MviFragmentActivityInterface::class.java.name} to use ${ComposeBaseFragment::class.java.name}")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setupFlowListener()
        return ComposeView(requireContext()).apply {
            setContent {
                FragmentStateContent()
            }
        }
    }

    abstract fun onSideEffectReceived(iSideEffect: ISideEffect): Boolean

    @Composable
    abstract fun FragmentContent(initialState: State<VS>)

    @Composable
    private fun FragmentStateContent() {
        val vm = getVmInstance()
        val initialState = vm.states.collectAsStateWithLifecycle()
        FragmentContent(initialState)
    }

    private fun setupFlowListener() {
        val vm = getVmInstance()
        viewLifecycleOwner.lifecycleScope.launch {
            vm.effects.collect {
                val handled = onSideEffectReceived(it)
                if (handled.not()) (activity as? MviFragmentActivityInterface)?.onSideEffectReceived(
                    it
                )
            }
        }
    }
}
