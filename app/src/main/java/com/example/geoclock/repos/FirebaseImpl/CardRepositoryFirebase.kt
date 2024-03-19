package com.example.geoclock.repos.FirebaseImpl

import androidx.lifecycle.MutableLiveData
import com.example.geoclock.model.Card
import com.example.geoclock.repos.CardRepository
import com.example.geoclock.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import safeCall

class CardRepositoryFirebase : CardRepository {

    private val cardRef = FirebaseFirestore.getInstance().collection("cards")
    override suspend fun addCard(title: String) = withContext(Dispatchers.IO){
        safeCall{
            val cardId = cardRef.document().id
            val card = Card(cardId, title)
            val addition = cardRef.document(cardId).set(card).await()
            Resource.Success(addition)
        }
    }

    override suspend fun deleteCard(cardId: String) = withContext(Dispatchers.IO){
        safeCall {
            val result = cardRef.document(cardId).delete().await()
            Resource.Success(result)
        }
    }

    override suspend fun setDate(cardId: String, date:Int) = withContext(Dispatchers.IO){
        safeCall {
            val result = cardRef.document(cardId).update("date", Int).await()
            Resource.Success(result)
        }
    }

    override suspend fun getCard(cardId: String) = withContext(Dispatchers.IO) {
        safeCall {
            val result = cardRef.document(cardId).get().await()
            val card = result.toObject(Card::class.java)
            Resource.Success(card!!)
        }
    }

    override suspend fun getCards() = withContext(Dispatchers.IO){
        safeCall {
            val result = cardRef.get().await()
            val cards = result.toObjects(Card::class.java)
            Resource.Success(cards)
        }
    }

    override fun getCardFlow(): Flow<Resource<List<Card>>> {
        TODO("Not yet implemented")
    }

    override fun getCardsLiveData(data: MutableLiveData<Resource<List<Card>>>) {
        data.postValue(Resource.Loading())

        cardRef.orderBy("title").addSnapshotListener {snapshot, e->
            if(e!=null){
                data.postValue(Resource.Error(e.localizedMessage))
            }
            if(snapshot != null && !snapshot.isEmpty){
                data.postValue(Resource.Success(snapshot.toObjects(Card::class.java)))
            }
            else{
                data.postValue(Resource.Error("No data"))
            }
        }
    }
}