package com.secret.palpatine.data.model.friends.friend

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * Handles the connection and exchange with the friends database.
 *
 * @property db Connection to the database
 */
class FriendRepository {

    val db = Firebase.firestore

    fun getUserFriends(currentUser: FirebaseUser): Task<QuerySnapshot> {
        val userRef = db.collection("users").document(currentUser.uid);
        return db.collection("users").whereArrayContains("friends", userRef).get()
    }

    fun getFriendRequests(currentUser: FirebaseUser): Task<QuerySnapshot> {
        val userRef = db.collection("users").document(currentUser.uid);
        return db.collection("friendships").whereEqualTo("friend", userRef)
            .whereEqualTo("state", "requested").get()
    }

    fun getUserByReference(userRef: DocumentReference): Task<DocumentSnapshot> {
        return userRef.get()
    }

    fun acceptAsFriend(requestId: String): Task<Void> {
        val data = hashMapOf("state" to "friends")
        return db.collection("friendships").document(requestId).set(data, SetOptions.merge())
    }


    fun searchForFriends(query: String): Task<QuerySnapshot> {
        return db.collection("users").whereGreaterThanOrEqualTo("username", query).get()
    }
}
