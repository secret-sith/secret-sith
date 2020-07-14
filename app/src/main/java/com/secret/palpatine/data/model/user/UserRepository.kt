package com.secret.palpatine.data.model.user

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * Created by Florian Fuchs on 19.06.2020.
 */
class UserRepository {

    val db = Firebase.firestore


    fun updateUserAfterSignup(userId: String, userName: String): Task<Void> {

        val userMap = hashMapOf(
            "name" to userName,
            "username" to userName
        )

        return db.collection("users").document(userId).set(userMap, SetOptions.merge())

    }

    fun getLiveUser(userId: String): DocumentReference {

        return db.collection("users").document(userId);

    }

    fun getUserByReference(userId: String): Task<DocumentSnapshot> {

        return db.collection("users").document(userId).get();

    }

    fun endGameForPlayer(userId: String): Task<Void>{
        val userMap = hashMapOf(
            "currentGame" to null
        )
        return db.collection("users").document(userId).set(userMap, SetOptions.merge())

    }
}