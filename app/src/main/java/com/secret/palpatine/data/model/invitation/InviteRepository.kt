package com.secret.palpatine.data.model.invitation

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * Handles the connection and exchange with the invite service
 *
 * @property db Connection to the database
 */
class InviteRepository {

    val db = Firebase.firestore

    /**
     * Provided a [userId] retrieves all associated invitation documents from the database.
     */
    fun getInvites(userId: String): Query {
        return db.collection("invites").whereEqualTo("to", userId)
    }

    /**
     * Given an valid [invite] the corresponding state will be changed to accepted.
     */
    fun acceptInvite(invite: Invite): Task<Void> {
        val data = hashMapOf(
            "state" to "accepted"
        )
       return  db.collection("invites").document(invite.id).set(data, SetOptions.merge())
    }
}