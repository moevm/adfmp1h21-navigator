package com.example.androidnavigatorleti.domain.use_case

import com.example.androidnavigatorleti.common.Resource
import com.example.androidnavigatorleti.data.room.tables.DbUserInfo
import com.example.androidnavigatorleti.domain.model.UserInfo
import com.example.androidnavigatorleti.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class InsertUserUseCase(
    private val repository: UserRepository
) {
    operator fun invoke(userInfo: UserInfo): Flow<Resource<Unit>> = flow {
        try {
            emit(Resource.Loading())
            val result = repository.insertUser(DbUserInfo(userInfo))
            emit(Resource.Success(result))
        } catch(e: Exception) {
            emit(Resource.Error<Unit>(e.localizedMessage ?: "An unexpected error occured"))
        }
    }
}