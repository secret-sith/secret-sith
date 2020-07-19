package com.secret.palpatine.data.model.user

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * Handles the connection and exchange with the user database.
 *
 * @property db Connection to the database
 */
class UserRepository {
    val db = Firebase.firestore

    /**
     * Adds the username of the player to the database accordingly
     */
    fun updateUserAfterSignup(userId: String, userName: String): Task<Void> {
        val userMap = hashMapOf(
            "name" to userName,
            "username" to userName
        )
        return db.collection("users").document(userId).set(userMap, SetOptions.merge())
    }

    /**
     * Provided a [userId] returns the matching document reference
     */
    fun getLiveUser(userId: String): DocumentReference {
        return db.collection("users").document(userId);
    }

    /**
     * Retrieve a user document by [userId].
     */
    fun getUserByReference(userId: String): Task<DocumentSnapshot> {
        return db.collection("users").document(userId).get();
    }

    /**
     * Resets the current game for the user identified by [userId].
     */
    fun endGameForPlayer(userId: String): Task<Void> {
        val userMap = hashMapOf(
            "currentGame" to null
        )
        return db.collection("users").document(userId).set(userMap, SetOptions.merge())
    }
}