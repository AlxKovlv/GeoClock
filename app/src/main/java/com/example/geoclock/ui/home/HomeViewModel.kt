package com.example.geoclock.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.geoclock.repos.AuthRepository

class HomeViewModel(private val authRep:AuthRepository) : ViewModel() {

    fun sighOut(){
        authRep.logout()
    }

    class HomeViewModelFactory(private val authRep: AuthRepository) : ViewModelProvider.NewInstanceFactory(){
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            return HomeViewModel(authRep) as T
        }
    }
}