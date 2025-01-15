package com.example.submissionexpert1.domain.repository.user

import com.example.submissionexpert1.domain.common.Result
import com.example.submissionexpert1.domain.model.User
import kotlinx.coroutines.flow.Flow

interface IAuthRepository {

  suspend fun login(email : String, password : String) : Flow<Result<String>>
  suspend fun register(user : User) : Flow<Result<String>>
  suspend fun update(user : User, newPassword : String) : Flow<Result<String>>
}