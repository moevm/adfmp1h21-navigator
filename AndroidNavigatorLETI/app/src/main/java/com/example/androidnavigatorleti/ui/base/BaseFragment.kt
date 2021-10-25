package com.example.androidnavigatorleti.ui.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.example.androidnavigatorleti.MainActivity
import com.example.androidnavigatorleti.navigateTo
import com.instacart.library.truetime.TrueTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent

abstract class BaseFragment<T : BaseViewModel<S>, S : BaseViewState>(
    @LayoutRes private val layoutId: Int,
    private val disableAdjustResize: Boolean = false
) : Fragment(layoutId), KoinComponent {

    companion object {

        const val DEFAULT_USER_LATITUDE = 30.315492
        const val DEFAULT_USER_LONGITUDE = 59.939007

        private const val CLICK_LOCKING_TIME = 500L
    }

    protected abstract val viewModel: T

    protected open val isNavigationIconEnabled: Boolean = true

    protected var toolbarTitle: TextView? = null
        private set

    protected val mainActivity: MainActivity
        get() = requireActivity() as MainActivity

    private var isWasClicked = false

    abstract fun renderState(state: S)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBaseObservers()
        initTrueTime()
    }

    protected fun openFragment(resId: Int) {
        with(findNavController()) {
            try {
                navigate(resId)
            } catch (e: IllegalArgumentException) {
            }
        }
    }

    protected fun openFragment(direction: NavDirections) {
        with(findNavController()) {
            try {
                navigate(direction)
            } catch (e: IllegalArgumentException) {
            }
        }
    }

    protected fun openKeyboard(viewToFocus: View) {
        viewToFocus.requestFocus()
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(viewToFocus, InputMethodManager.SHOW_IMPLICIT)
    }

    protected fun hideKeyboard() {
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val viewAtFocus = requireActivity().currentFocus
        inputMethodManager.hideSoftInputFromWindow(viewAtFocus?.windowToken, 0)
    }

    protected fun checkDoubleClick(click: () -> Unit) {
        if (!isWasClicked) {
            isWasClicked = true
            click.invoke()
            Handler(Looper.getMainLooper()).postDelayed({
                isWasClicked = false
            }, CLICK_LOCKING_TIME)
        }
    }

    protected fun restartApp(action: String? = null) {
        val i = requireContext().packageManager
            .getLaunchIntentForPackage(requireContext().packageName)?.apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                if (action != null) setAction(action)
                putExtra("open_app", true)
            }
        startActivity(i)
    }

    private fun initBaseObservers() {
        viewModel.stateLiveData.observe(
            viewLifecycleOwner,
            Observer(::renderState)
        )

        viewModel.navigationLiveData.observe(
            viewLifecycleOwner,
            EventObserver { findNavController().navigateTo(it) }
        )
    }

    private fun initTrueTime() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    if (!TrueTime.isInitialized()) {
                        TrueTime.build()
                            .withNtpHost("time.apple.com")
                            .withRootDelayMax(750F)
                            .withRootDispersionMax(100F)
                            .withServerResponseDelayMax(750)
                            .initialize()
                    } else {
                        Log.d("initTimeException", "already initialized")
                    }
                } catch (e: Exception) {
                    Log.e("initTimeException", "Init true time exception: $e")
                }
            }
        }
    }
}