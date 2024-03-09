package com.example.geoclock.repos

import com.example.geoclock.model.User
import com.example.geoclock.util.Resource

interface AuthRepository {

    suspend fun currentUser() : Resource<User>
    suspend fun login(email:String, password:String) : Resource<User>
    suspend fun createUser(userName:String, userEmail:String, userPhone:String, userLoginPass:String) : Resource<User>
    fun logout()
}