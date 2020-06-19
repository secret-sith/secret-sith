package com.secret.palpatine.data.model.friends.friend

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * Created by Florian Fuchs on 08.06.2020.
 */
class FriendRepository {

    val db = Firebase.firestore


    fun getUserFriends(currentUser: FirebaseUser): Task<QuerySnapshot> {

        var userRef = db.collection("users").document(currentUser.uid);

        return db.collection("users").whereArrayContains("friends", userRef).get()
    }

    fun getFriendRequests(currentUser: FirebaseUser): Task<QuerySnapshot> {

        var userRef = db.collection("users").document(currentUser.uid);
        return db.collection("friendships").whereEqualTo("friend", userRef)
            .whereEqualTo("state", "requested").get()
    }

    fun getUserByReference (userRef: DocumentReference): Task<DocumentSnapshot>{

        return userRef.get()

    }

    fun acceptAsFriend (requestId: String) :Task<Void>{
        val data = hashMapOf("state" to "friends")
        return db.collection("friendships").document(requestId).set(data, SetOptions.merge())

    }

}
