package com.example.geoclock.repos.firebaseImpl

import android.util.Log
import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.example.geoclock.model.Card
import com.example.geoclock.repos.CardRepository
import com.example.geoclock.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

import kotlinx.coroutines.launch

import safeCall

class CardRepositoryFirebase : CardRepository {

    private val cardRef = FirebaseFirestore.getInstance().collection("cards")

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

    //Function used to retrieve either all cards (if the user is admin) or only the currently logged in user's cards from firestore
    override fun getCardsLiveData(data: MutableLiveData<Resource<List<Card>>>, coroutineScope: CoroutineScope) {
        data.postValue(Resource.Loading())
        coroutineScope.launch {
            val currentUserResult = withContext(Dispatchers.IO) {
                AuthRepositoryFirebase().currentUser()
            }
            if (currentUserResult is Resource.Success) {
                val currentUser = currentUserResult.data
                val userName = currentUser?.name ?: "Unknown User"
                //Fetch all cards from Firestore
                cardRef.orderBy("time")
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            data.postValue(Resource.Error(e.localizedMessage ?: "Unknown error"))
                        }
                        if (snapshot != null && !snapshot.isEmpty) {
                            //Filter cards based on the current user's name
                            val filteredCards = if (!userName.contains("admin", ignoreCase = true)) {
                                snapshot.documents.mapNotNull { document ->
                                    document.toObject(Card::class.java)?.takeIf { it.userName == userName }
                                }
                            } else {
                                snapshot.toObjects(Card::class.java)
                            }
                            //Sort the filtered cards by date in descending order
                            val sortedFilteredCards = filteredCards.sortedBy { it.time }.sortedBy { formatDateForFirestore(it.date) }
                            data.postValue(Resource.Success(sortedFilteredCards))
                        } else {
                            //Found no cards
                            data.postValue(Resource.Error("No data"))
                        }
                    }
            } else {
                //Could not fetch user
                data.postValue(Resource.Error("Failed to fetch current user"))
            }
        }
    }

    //Function used to get only cards within a given date range
    override suspend fun getCardsInRange(startDate: String, endDate: String): Resource<List<Card>> {
        return safeCall {
            //First get the current user to see if he's admin or not
            val currentUserResult = AuthRepositoryFirebase().currentUser()
            if (currentUserResult is Resource.Success) {
                val currentUser = currentUserResult.data
                val userName = currentUser?.name ?: "Unknown User"
                val isAdmin = userName.contains("admin", ignoreCase = true)
                //Fetch all cards from firestore
                val querySnapshot = cardRef.get().await()
                //Convert the snapshot to a list of cards
                val cards = querySnapshot.toObjects(Card::class.java)
                //Filter cards based on the date range and user role
                val filteredCards = cards.filter { card ->
                    //Format the existing cards dates in order to compare with formatted end and start dates
                    val formattedCardDate = formatDateForFirestore(card.date)
                    val formattedStartDate = formatDateForFirestore(startDate)
                    val formattedEndDate = formatDateForFirestore(endDate)
                    if (isAdmin) {
                        true //If user is admin, include all cards
                    } else {//Otherwise, include only cards associated with the current user
                        card.userName == userName
                    } && formattedCardDate >= formattedStartDate && formattedCardDate <= formattedEndDate
                }
                //Sort the filtered cards by time and date in ascending order
                val sortedFilteredCards = filteredCards.sortedBy { it.time }.sortedBy { formatDateForFirestore(it.date) }
                Resource.Success(sortedFilteredCards)
            } else {
                Resource.Error("Failed to fetch current user")
            }
        }
    }

    //Function used to format the dates to yyyyMMdd format instead of existing dd/MM/yyyy for firestore queries
    private fun formatDateForFirestore(date: String): String {
        val parts = date.split("/")
        val day = parts[0].padStart(2, '0')
        val month = parts[1].padStart(2, '0')
        val year = parts[2]
        return "$year$month$day"
    }
}