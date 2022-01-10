package com.example.androidnavigatorleti.domain.use_case

import com.example.androidnavigatorleti.common.Resource
import com.example.androidnavigatorleti.data.room.tables.DbSearchHistoryItem
import com.example.androidnavigatorleti.domain.model.SearchHistoryItem
import com.example.androidnavigatorleti.domain.repository.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AddSearchHistoryUseCase(
    private val repository: SearchHistoryRepository
) {
    operator fun invoke(item: SearchHistoryItem): Flow<Resource<Unit>> = flow {
        try {
            emit(Resource.Loading())
            val result = repository.addSearchHistoryItem(DbSearchHistoryItem(item))
            emit(Resource.Success(result))
        } catch(e: Exception) {
            emit(Resource.Error<Unit>(e.localizedMessage ?: "An unexpected error occured"))
        }
    }
}