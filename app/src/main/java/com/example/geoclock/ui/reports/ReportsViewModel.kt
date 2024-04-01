package com.example.geoclock.ui.reports

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.geoclock.model.Card
import com.example.geoclock.repos.CardRepository
import com.example.geoclock.util.Resource
import kotlinx.coroutines.launch

class ReportsViewModel(private val cardRepository: CardRepository) : ViewModel() {

    private val _filteredCards = MutableLiveData<Resource<List<Card>>>()
    val filteredCards: LiveData<Resource<List<Card>>> = _filteredCards

    fun getFilteredCards(startDate: String, endDate: String) {
        viewModelScope.launch {
            //Fetch cards based on the date range
            val result = cardRepository.getCardsInRange(startDate, endDate)
            _filteredCards.postValue(result)
        }
    }

    class ReportsViewModelFactory(private val cardRepository: CardRepository) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            return ReportsViewModel(cardRepository) as T
        }
    }
}