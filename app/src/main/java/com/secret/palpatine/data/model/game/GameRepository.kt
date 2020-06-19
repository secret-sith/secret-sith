package com.secret.palpatine.data.model.game

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.secret.palpatine.data.model.player.PlayerState
import com.secret.palpatine.data.model.user.User

/**
 * Created by Florian Fuchs on 19.06.2020.
 */

class GameRepository {

    val db = Firebase.firestore


    fun getPlayers(gameId: String): CollectionReference {

        return db.collection("games").document(gameId).collection("players")
    }

    fun createGame(playerList: List<User>, userId: String, userName: String): Task<Void> {
        val game = hashMapOf(
            "loylistPolitics" to 0,
            "imperialPolitics" to 0,
            "failedGoverments" to 0,
            "state" to "pending",
            "phase" to "nominate_chancellor",
            "host" to userId

        )


        return db.collection("games").add(game).continueWithTask {
            if (it.exception != null) {
                throw it.exception!!
            } else {
                val id = it.result!!.id
                var plist: ArrayList<Task<DocumentReference>> = arrayListOf()

                for (player in playerList) {
                    val playerToAdd = hashMapOf(
                        "user" to player.id,
                        "inviteBy" to userName,
                        "inviteById" to userId,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "state" to PlayerState.pending,
                        "userName" to player.username
                    )
                    plist.add(
                        db.collection("games").document(id).collection("players").add(playerToAdd)
                    )
                }
                Tasks.whenAll(plist)
            }
        }
    }


    fun getGame(gameId: String): DocumentReference {

        return db.collection("games").document(gameId)
    }


}