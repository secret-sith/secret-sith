package com.secret.palpatine.data.model.game

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.secret.palpatine.data.model.PlayerRole
import com.secret.palpatine.data.model.player.Player
import com.secret.palpatine.data.model.player.PlayerState
import com.secret.palpatine.data.model.user.User

/**
 * Created by Florian Fuchs on 19.06.2020.
 */

class GameRepository {

    val db = Firebase.firestore


    fun startGame(gameId: String): Task<Void> {
        val data = hashMapOf(
            "state" to GameState.started
        )

        val gameRef = db.collection("games").document(gameId)
        val playerRef = this.getPlayers(gameId)
        val players = this.setPlayerRoles(playerRef.get())

        return db.runTransaction { transaction ->

            transaction.set(gameRef, data, SetOptions.merge())
            transaction.set(playerRef, players, SetOptions.merge())

            null
        }

        return db.collection("games").document(gameId).set(data, SetOptions.merge())
    }

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
            "host" to userId,
            "numberPlayers" to playerList.size + 1
        )

        val election = hashMapOf(
            "votes_left" to playerList.size + 1,
            "counter" to 0
        )
        game["election"] = election

        val host = hashMapOf(
            "user" to userId,
            "createdAt" to FieldValue.serverTimestamp(),
            "state" to PlayerState.accepted,
            "userName" to userName,
            "isHost" to true
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
                plist.add(
                    db.collection("games").document(id).collection("players").add(host)
                )
                Tasks.whenAll(plist)
            }
        }
    }


    fun getGame(gameId: String): DocumentReference {

        return db.collection("games").document(gameId)
    }


    fun joinGame(gameId: String, userId: String, userName: String): Task<Void> {

        val data = hashMapOf(
            "user" to userId,
            "createdAt" to FieldValue.serverTimestamp(),
            "state" to PlayerState.accepted,
            "userName" to userName
        )
        return db.collection("games").document(gameId).collection("players")
            .add(data).continueWithTask {

                val userUpdate = hashMapOf(
                    "currentGame" to gameId
                )
                if (it.exception != null) {
                    throw it.exception!!
                } else {
                    db.collection("users").document(userId).set(userUpdate, SetOptions.merge())
                }
            }
    }

   fun setPlayerRoles(players: List<Player>): List<Player> {
       val playerRoleList: List<PlayerRole>

       if (players.size == 5){
            playerRoleList = listOf(
                PlayerRole.loyalist,
                PlayerRole.loyalist,
                PlayerRole.loyalist,
                PlayerRole.imperialist,
                PlayerRole.sith).shuffled()
        }
        else {
            throw Exception("Number of players is not supported.")
        }

        for (i in 0..players.size){
            players[i].role = playerRoleList[i]
        }

       return players
    }

}