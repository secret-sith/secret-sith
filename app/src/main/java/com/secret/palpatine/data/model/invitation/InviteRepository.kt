package com.secret.palpatine.data.model.invitation

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * Created by Florain Fuchs on 20.06.2020.
 */
class InviteRepository {

    val db = Firebase.firestore

    fun getInvites(userId: String): Query {

        return db.collection("invites").whereEqualTo("to", userId)
    }

    fun acceptInvite(invite: Invite) {

        val data = hashMapOf(
            "state" to "accepted"
        )
        db.collection("invites").document(invite.id).set(data, SetOptions.merge())
    }
}