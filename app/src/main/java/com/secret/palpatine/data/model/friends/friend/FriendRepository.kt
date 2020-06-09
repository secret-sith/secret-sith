package com.secret.palpatine.data.model.friends.friend

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.secret.palpatine.data.model.User

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

}
