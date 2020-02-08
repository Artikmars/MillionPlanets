package com.artamonov.millionplanets.repository
import com.artamonov.millionplanets.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {

    private val firestoreInstance = FirebaseFirestore.getInstance()
    private val userCollection = firestoreInstance.collection("Objects")

    fun getUserFromFirestore(userName: String): Task<User> {
        val documentSnapshot = userCollection.document(userName).get()
        return documentSnapshot.continueWith {
            if (documentSnapshot.isSuccessful) {
                return@continueWith documentSnapshot.result?.toObject(User::class.java)
            } else {
                return@continueWith null
            }
        }
    }
}