package com.example.androidnavigatorleti.domain.use_case

import com.example.androidnavigatorleti.common.Resource
import com.example.androidnavigatorleti.domain.model.SearchHistoryItem
import com.example.androidnavigatorleti.domain.repository.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class GetSearchHistoryUseCase(
    private val repository: SearchHistoryRepository
) {
    operator fun invoke(): Flow<Resource<List<SearchHistoryItem>>> = flow {
        try {
            emit(Resource.Loading())
            val result = repository.getSearchHistory().map {
                it.mapToDomain()
            }
            emit(Resource.Success(result))
        } catch(e: Exception) {
            emit(Resource.Error<List<SearchHistoryItem>>(e.localizedMessage ?: "An unexpected error occured"))
        }
    }
}