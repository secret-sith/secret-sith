package com.secret.palpatine.data.model.invitation

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * Created by Florian Fuchs on 20.06.2020.
 */
class InviteRepository {

    val db = Firebase.firestore

    fun getInvites(userId: String): Query {

        return db.collection("invites").whereEqualTo("to", userId)
    }

    fun acceptInvite(invite: Invite): Task<Void> {

        val data = hashMapOf(
            "state" to "accepted"
        )
       return  db.collection("invites").document(invite.id).set(data, SetOptions.merge())
    }
}