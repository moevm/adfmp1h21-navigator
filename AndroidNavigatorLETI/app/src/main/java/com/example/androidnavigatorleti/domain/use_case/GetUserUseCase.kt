package com.example.androidnavigatorleti.domain.use_case

import com.example.androidnavigatorleti.common.Resource
import com.example.androidnavigatorleti.domain.model.UserInfo
import com.example.androidnavigatorleti.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetUserUseCase(
    private val repository: UserRepository
) {
    operator fun invoke(): Flow<Resource<UserInfo>> = flow {
        try {
            emit(Resource.Loading())
            val result = repository.getUserOrNull()?.mapToDomain()
            result?.let {
                emit(Resource.Success(it))
            } ?: emit(Resource.Error<UserInfo>("user is null"))
        } catch(e: Exception) {
            emit(Resource.Error<UserInfo>(e.localizedMessage ?: "An unexpected error occured"))
        }
    }
}