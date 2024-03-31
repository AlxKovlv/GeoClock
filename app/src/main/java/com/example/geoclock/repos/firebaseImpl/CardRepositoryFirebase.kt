package com.example.geoclock.repos.firebaseImpl

import android.util.Log
import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.example.geoclock.model.Card
import com.example.geoclock.repos.CardRepository
import com.example.geoclock.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

import kotlinx.coroutines.launch

import safeCall

class CardRepositoryFirebase : CardRepository {

    private val cardRef = FirebaseFirestore.getInstance().collection("cards")

//    override suspend fun addCard(title: String, date: String, time: String) = withContext(Dispatchers.IO) {
//        safeCall {
//            val currentUserResult = AuthRepositoryFirebase().currentUser()
//            if (currentUserResult is Resource.Success) {
//                val currentUser = currentUserResult.data
//                val userName = currentUser?.name ?: "Unknown User"
//                val cardId = cardRef.document().id
//                val card = Card(cardId, title, userName, date, time) // Include time parameter
//                val addition = cardRef.document(cardId).set(card).await()
//                Resource.Success(addition)
//            } else {
//                Resource.Error("Failed to fetch current user")
//            }
//        }
//    }

    override suspend fun addCard(title: String, date: String, time: String, location: String,note:String ,photo: String) = withContext(Dispatchers.IO) {
        safeCall {
            val currentUserResult = AuthRepositoryFirebase().currentUser()
            if (currentUserResult is Resource.Success) {
                val currentUser = currentUserResult.data
                val userName = currentUser?.name ?: "Unknown User"
                val cardId = cardRef.document().id
                val card = Card(cardId, title, userName, date, time, location,0.0,0.0,note,photo) // Include location parameter
                val addition = cardRef.document(cardId).set(card).await()
                Resource.Success(addition)
            } else {
                Resource.Error("Failed to fetch current user")
            }
        }
    }


    override suspend fun deleteCard(cardId: String) = withContext(Dispatchers.IO){
        safeCall {
            val result = cardRef.document(cardId).delete().await()
            Resource.Success(result)
        }
    }

    override suspend fun setDate(cardId: String, date: String) = withContext(Dispatchers.IO){
        safeCall {
            val result = cardRef.document(cardId).update("date", date).await()
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

//Old way of getting cards which gets all cards no matter which user it is
//    override fun getCardsLiveData(data: MutableLiveData<Resource<List<Card>>>) {
//        data.postValue(Resource.Loading())
//
//        cardRef.orderBy("time").addSnapshotListener {snapshot, e->
//            if(e!=null){
//                data.postValue(Resource.Error(e.localizedMessage ?: "Unknown error"))
//            }
//            if(snapshot != null && !snapshot.isEmpty){
//                data.postValue(Resource.Success(snapshot.toObjects(Card::class.java)))
//            }
//            else{
//                data.postValue(Resource.Error("No data"))
//            }
//        }
//    }


    //My modified version
    override fun getCardsLiveData(data: MutableLiveData<Resource<List<Card>>>) {
        data.postValue(Resource.Loading())

        // Get the current user to filter cards by their name
        GlobalScope.launch {
            val currentUserResult = withContext(Dispatchers.IO) {
                AuthRepositoryFirebase().currentUser()
            }
            if (currentUserResult is Resource.Success) {
                val currentUser = currentUserResult.data
                val userName = currentUser?.name ?: "Unknown User"

                // Fetch all cards from Firestore
                cardRef.orderBy("time")
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            data.postValue(Resource.Error(e.localizedMessage ?: "Unknown error"))
                        }
                        if (snapshot != null && !snapshot.isEmpty) {
                            // Filter cards based on the current user's name
                            if (!userName.contains("admin", ignoreCase = true)){
                                val filteredCards = snapshot.documents.mapNotNull { document ->
                                    document.toObject(Card::class.java)?.takeIf { it.userName == userName }
                                }
                                data.postValue(Resource.Success(filteredCards))
                            }
                            else{//Admin user
                                data.postValue(Resource.Success(snapshot.toObjects(Card::class.java)))
                            }

                        } else {
                            data.postValue(Resource.Error("No data"))
                        }
                    }
            } else {
                data.postValue(Resource.Error("Failed to fetch current user"))
            }
        }
    }

    override suspend fun getCardsInRange(startDate: String, endDate: String): Resource<List<Card>> {
        return try {
            // Get the current user to filter cards by their name
            val currentUserResult = AuthRepositoryFirebase().currentUser()
            if (currentUserResult is Resource.Success) {
                val currentUser = currentUserResult.data
                val userName = currentUser?.name ?: "Unknown User"

                // Query Firestore to get the cards within the date range for the current user
                val querySnapshot = cardRef
                    .whereEqualTo("userName", userName)
                    .whereGreaterThanOrEqualTo("date", startDate)
                    .whereLessThanOrEqualTo("date", endDate)
                    .get()
                    .await()

                // Convert the query snapshot to a list of cards
                val cardsInRange = querySnapshot.toObjects(Card::class.java)
                Log.d("TAG", "Amount of cards with your dates: ${cardsInRange.size}")


                Resource.Success(cardsInRange)
            } else {
                Resource.Error("Failed to fetch current user")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
}