package com.example.geoclock.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.geoclock.model.Card
import com.example.geoclock.repos.AuthRepository
import com.example.geoclock.repos.CardRepository
import com.example.geoclock.util.Resource
import kotlinx.coroutines.launch

class HomeViewModel(private val authRep:AuthRepository, val cardRep:CardRepository) : ViewModel() {

    val _cardsStatus : MutableLiveData<Resource<List<Card>>> = MutableLiveData()
    val cardStatus : LiveData<Resource<List<Card>>> = _cardsStatus

    private val _addCardStatus = MutableLiveData<Resource<Void>>()
    val addCardStatus: LiveData<Resource<Void>> = _addCardStatus

    private val _deleteCardStatus = MutableLiveData<Resource<Void>>()
    val deleteCardStatus:LiveData<Resource<Void>> = _deleteCardStatus
    fun sighOut(){
        authRep.logout()
    }

    fun addCard(title:String){
        viewModelScope.launch {
            if(title.isEmpty()) _addCardStatus.postValue(Resource.Error("Empty title"))
            else {
                _addCardStatus.postValue(Resource.Loading())
                _addCardStatus.postValue(cardRep.addCard(title))
            }
        }
    }

    fun deleteCard(cardId:String){
        viewModelScope.launch {
            if(cardId.isEmpty()) _addCardStatus.postValue(Resource.Error("Empty card id"))
            else {
                _addCardStatus.postValue(Resource.Loading())
                _addCardStatus.postValue(cardRep.addCard(cardId))
            }
        }
    }

    fun setDate(cardId:String, date:Int){
        viewModelScope.launch {
            cardRep.setDate(cardId, date)
        }
    }

    init {
        cardRep.getCardsLiveData(_cardsStatus)
    }

    class HomeViewModelFactory(private val authRep: AuthRepository, val cardRep:CardRepository) : ViewModelProvider.NewInstanceFactory(){
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            return HomeViewModel(authRep, cardRep) as T
        }
    }
}