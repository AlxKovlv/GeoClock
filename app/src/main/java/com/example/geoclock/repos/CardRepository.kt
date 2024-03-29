package com.example.geoclock.repos

import androidx.lifecycle.MutableLiveData
import com.example.geoclock.model.Card
import com.example.geoclock.util.Resource

interface CardRepository {

//    suspend fun addCard(title: String, date: String, time: String): Resource<Void>
    suspend fun addCard(title: String, date: String, time: String, location: String): Resource<Void>

    suspend fun deleteCard(cardId:String) : Resource<Void>
    suspend fun getCard(cardId:String) : Resource<Card>
    suspend fun setDate(cardId:String, date:String) : Resource<Void>
    suspend fun getCards() : Resource<List<Card>>
    fun getCardsLiveData(data: MutableLiveData<Resource<List<Card>>>)

}