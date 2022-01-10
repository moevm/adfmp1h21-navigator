package com.example.androidnavigatorleti.common.base

import androidx.annotation.IdRes
import androidx.annotation.UiThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.component.KoinComponent
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel<T : BaseViewState>(private val initialState: T) : ViewModel(), KoinComponent, CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + SupervisorJob()

    protected val stateMutableLiveData: MutableLiveData<T> = MutableLiveData<T>().apply {
        value = initialState
    }

    val stateLiveData: LiveData<T>
        get() = stateMutableLiveData

    protected val currentState: T
        get() = stateMutableLiveData.value ?: initialState

    private val navigationMutableLiveData = MutableLiveData<Event<Navigation>>()
    val navigationLiveData: LiveData<Event<Navigation>>
        get() = navigationMutableLiveData

    @UiThread
    protected inline fun updateState(update: (currentState: T) -> T) {
        val updatedState: T = update(currentState)
        if (updatedState != currentState) {
            stateMutableLiveData.value = updatedState
        }
    }

    @UiThread
    protected fun navigate(navDirections: NavDirections) {
        navigationMutableLiveData.value = Event(Navigation.To(navDirections))
    }

    @UiThread
    protected fun navigateBack() {
        navigationMutableLiveData.value = Event(Navigation.Back)
    }

    @UiThread
    protected fun navigateBack(@IdRes destinationId: Int, inclusive: Boolean) {
        navigationMutableLiveData.value = Event(Navigation.BackTo(destinationId, inclusive))
    }
}

interface BaseViewState

class EmptyViewState: BaseViewState