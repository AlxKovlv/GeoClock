package com.example.geoclock.ui.home

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.geoclock.model.Card
import com.example.geoclock.model.User
import com.example.geoclock.repos.AuthRepository
import com.example.geoclock.repos.CardRepository
import com.example.geoclock.util.Resource
import kotlinx.coroutines.launch

class HomeViewModel(private val authRep:AuthRepository, val cardRep:CardRepository) : ViewModel() {

    private val _cardsStatus : MutableLiveData<Resource<List<Card>>> = MutableLiveData()
    val cardStatus : LiveData<Resource<List<Card>>> = _cardsStatus

    private val _addCardStatus = MutableLiveData<Resource<Void>>()
    val addCardStatus: LiveData<Resource<Void>> = _addCardStatus

    private val _deleteCardStatus = MutableLiveData<Resource<Void>>()
    val deleteCardStatus:LiveData<Resource<Void>> = _deleteCardStatus

    private val _currentUser = MutableLiveData<Resource<User>>()
    val currentUser: LiveData<Resource<User>> = _currentUser

    fun fetchCurrentUser() {
        viewModelScope.launch {
            _currentUser.value = authRep.currentUser()
        }
    }

    fun getDefaultTitle(callback: (String) -> Unit) {
        viewModelScope.launch {
            val defaultTitleDocumentId = "your_default_title_document_id"
            val defaultTitleResult = cardRep.getCard(defaultTitleDocumentId)
            if (defaultTitleResult is Resource.Success) {
                val defaultTitle = defaultTitleResult.data?.title ?: "Default Title"
                callback(defaultTitle)
            } else {
                callback("Default Title")//Default title
            }
        }
    }
    fun signOut(){
        authRep.logout()
    }


    //    fun addCard(title: String, date: String, time: String, location: String) {
//        viewModelScope.launch {
//            if (title.isEmpty()) {
//                _addCardStatus.postValue(Resource.Error("Empty title"))
//            } else {
//                _addCardStatus.postValue(Resource.Loading())
//                _addCardStatus.postValue(cardRep.addCard(title, date, time, location))
//            }
//        }
//    }
    fun addCard(title: String, date: String, time: String, location: String,note:String ,photo: String) {
        viewModelScope.launch {
            if (title.isEmpty()) {
                _addCardStatus.postValue(Resource.Error("Empty title"))
            } else {
                _addCardStatus.postValue(Resource.Loading())
                _addCardStatus.postValue(cardRep.addCard(title, date, time, location,note,photo))
            }
        }
    }


    fun deleteCard(cardId:String){
        viewModelScope.launch {
            if(cardId.isEmpty()) {
                _deleteCardStatus.postValue(Resource.Error("Empty card id"))
            } else {
                _deleteCardStatus.postValue(Resource.Loading())
                val result = cardRep.deleteCard(cardId)
                if (result is Resource.Success) {
                    val currentCards = _cardsStatus.value?.data?.toMutableList() ?: mutableListOf()
                    currentCards.removeAll { it.cardId == cardId }
                    _cardsStatus.postValue(Resource.Success(currentCards))
                }
                _deleteCardStatus.postValue(result)
            }
        }
    }



    fun setDate(cardId:String, date: String){
        viewModelScope.launch {
            cardRep.setDate(cardId, date)
        }
    }

    init {
        cardRep.getCardsLiveData(_cardsStatus)
    }

    class HomeViewModelFactory(private val authRep: AuthRepository, private val cardRep:CardRepository) : ViewModelProvider.NewInstanceFactory(){
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            return HomeViewModel(authRep, cardRep) as T
        }
    }
}