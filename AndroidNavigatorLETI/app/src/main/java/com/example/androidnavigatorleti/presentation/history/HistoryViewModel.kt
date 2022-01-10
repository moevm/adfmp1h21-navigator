package com.example.androidnavigatorleti.presentation.history

import androidx.lifecycle.viewModelScope
import com.example.androidnavigatorleti.common.base.BaseViewModel
import com.example.androidnavigatorleti.common.base.BaseViewState
import com.example.androidnavigatorleti.domain.model.SearchHistoryItem
import com.example.androidnavigatorleti.domain.model.UserInfo
import com.example.androidnavigatorleti.domain.use_case.DeleteSearchHistoryUseCase
import com.example.androidnavigatorleti.domain.use_case.GetSearchHistoryUseCase
import com.example.androidnavigatorleti.domain.use_case.GetUserUseCase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.core.component.inject

class HistoryViewModel : BaseViewModel<HistoryViewState>(HistoryViewState()) {

    private val getUserUc by inject<GetUserUseCase>()
    private val getSearchHistoryUc by inject<GetSearchHistoryUseCase>()
    private val deleteSearchHistoryUc by inject<DeleteSearchHistoryUseCase>()

    init {
        getUser()
    }

    fun getSearchHistory() {
        viewModelScope.launch {
            getSearchHistoryUc.invoke().collect { result ->
                updateState {
                    it.copy(
                        history = result.data,
                        isDeleted = false
                    )
                }
            }
        }
    }

    fun deleteItem(item: SearchHistoryItem) {
        viewModelScope.launch {
            deleteSearchHistoryUc.invoke(item).collect {
                updateState {
                    it.copy(isDeleted = true)
                }
                //TODO(Log result)
            }
        }
    }

    private fun getUser() {
        viewModelScope.launch {
            getUserUc.invoke().collect { result ->
                updateState {
                    it.copy(
                        userInfo = result.data,
                        isDeleted = false
                    )
                }
            }
        }
    }
}

data class HistoryViewState(
    val userInfo: UserInfo? = UserInfo(id = 0, birthDay = "", name = "", surname = ""),
    val history: List<SearchHistoryItem>? = null,
    val isDeleted: Boolean = false
): BaseViewState