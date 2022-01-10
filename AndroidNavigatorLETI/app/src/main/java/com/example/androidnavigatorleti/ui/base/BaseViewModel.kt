package com.example.androidnavigatorleti.ui.base

import androidx.annotation.IdRes
import androidx.annotation.UiThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import org.koin.core.component.KoinComponent

abstract class BaseViewModel<T : BaseViewState>(private val initialState: T) : ViewModel(), KoinComponent {

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