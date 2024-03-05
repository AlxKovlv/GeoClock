package com.example.geoclock.ui.register

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.geoclock.model.User
import com.example.geoclock.repos.AuthRepository
import com.example.geoclock.util.Resource
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository:AuthRepository) : ViewModel() {

    private val _userRegistrationStatus = MutableLiveData<Resource<User>>()
    val userRegistrationStatus: LiveData<Resource<User>> = _userRegistrationStatus

    fun createUser(userName:String, userEmail:String, userPhone:String, userPass:String){
        val error = if(userEmail.isEmpty() || userName.isEmpty() || userPass.isEmpty() || userPass.isEmpty())
            "Empty strings"
        else if(Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            "Not a valid email"
        }else null
        error?.let{
            _userRegistrationStatus.postValue(Resource.Error(it))
        }
        _userRegistrationStatus.value = Resource.Loading()
        viewModelScope.launch {
            val registrationResult = repository.createUser(userName, userEmail, userPhone, userPass)
            _userRegistrationStatus.postValue(registrationResult)
        }
    }

    class RegisterVieModelFactory(private val repo: AuthRepository) :ViewModelProvider.NewInstanceFactory(){
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RegisterViewModel(repo) as T
        }
    }
}