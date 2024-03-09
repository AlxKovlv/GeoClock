package com.example.geoclock.repos

import androidx.lifecycle.MutableLiveData
import com.example.geoclock.model.Card
import com.example.geoclock.util.Resource
import kotlinx.coroutines.flow.Flow

interface CardRepository {

    suspend fun addCard(index:Int) : Resource<Void>
    suspend fun deleteCard(index: Int) : Resource<Void>
    suspend fun getCard(index: Int) : Resource<Card>
    suspend fun getCards() : Resource<List<Card>>
    fun  getCardFlow() : Flow<Resource<List<Card>>>
    fun getCardsLiveData(data: MutableLiveData<Resource<List<Card>>>)

}