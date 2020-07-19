package com.secret.palpatine.data.model.game

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.secret.palpatine.data.model.player.PlayerRole
import com.secret.palpatine.data.model.player.Player
import com.secret.palpatine.data.model.player.PlayerState
import com.secret.palpatine.data.model.user.User
import com.secret.palpatine.util.*

/**
 * This class handles the communication from the local gamestate with the globale database.
 *
 * @property db Connection to the chosen database, which are Firebase services in this case
 */
class GameRepository {

    val db = Firebase.firestore

    /**
     * Updates the participating players in the game and sets global Gamestate to started.
     * Further the player roles and order are generated.
     *
     * @param gameId Unique ID of the game
     * @param players List of invited Players
     */
    fun startGame(gameId: String, players: List<Player>): Task<Void> {
        val gameRef = db.collection("games").document(gameId)
        Log.d("Players", players.size.toString())
        var groupedPlayersList: Map<PlayerState, List<Player>> = players.groupBy { it.state }
        var roleListPlayers: List<Player> =
            generatePlayerRolesAndOrder(
                groupedPlayersList[PlayerState.accepted] ?: error("No players available")
            )

        val batch = db.batch()


        for (player in roleListPlayers) {
            // Same for first player, then increment through for each
            var playerRef = buildPlayerRef(gameId, player.id)
            val playerData = hashMapOf(
                "role" to player.role,
                "order" to player.order
            )

            batch.set(playerRef, playerData, SetOptions.merge())

        }
        //Join other states and remove players from game
        var playersToRemove: MutableList<Player> =
            (groupedPlayersList[PlayerState.declined] ?: emptyList()).toMutableList()
        playersToRemove.addAll((groupedPlayersList[PlayerState.pending] ?: emptyList()))
        for (player in playersToRemove) {
            var playerRef = buildPlayerRef(gameId, player.id)
            batch.delete(playerRef)
        }

        val gameData = hashMapOf(
            "state" to GameState.started,
            "presidentialCandidate" to buildPlayerRef(
                gameId,
                players.findLast { player -> player.order == 0 }!!.id
            )
        )
        batch.set(gameRef, gameData, SetOptions.merge())
        return batch.commit()

    }

    /**
     * Given a unique [gameId] returns a list of all participating players.
     */
    fun getPlayers(gameId: String): CollectionReference {
        return db.collection("games").document(gameId).collection("players")
    }

    /**
     * Provided a [playerList] with invited users a Game is created in the database where the
     * [userId] combined with [userName] serves as identification of the host.
     */
    fun createGame(playerList: List<User>, userId: String, userName: String): Task<String> {
        val game: HashMap<String, Any?> = hashMapOf(
            "loyalistPolitics" to 0,
            "imperialPolitics" to 0,
            "failedGovernments" to 0,
            "state" to "pending",
            "phase" to "nominate_chancellor",
            "host" to userId,
            "numberPlayers" to playerList.size + 1,
            "president" to null,
            "chancellor" to null,
            "chancellorCandidate" to null,
            "presidentialCandidate" to null
        )

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
                        "userName" to player.username,
                        "vote" to null,
                        "order" to 0
                    )
                    plist.add(
                        db.collection("games").document(id).collection("players").add(playerToAdd)
                    )
                }
                plist.add(
                    db.collection("games").document(id).collection("players").add(host)
                )

                var drawpile = mutableListOf<HashMap<String, Any>>()
                for (i in 0..16) {
                    drawpile.add(
                        hashMapOf(
                            ORDER to 0,
                            TYPE to if (i < 6) LOYALIST else IMPERIALIST
                        )
                    )
                }
                drawpile.shuffle(); drawpile.shuffle(); drawpile.shuffle()
                for (i in 0..16) {
                    drawpile[i][ORDER] = i
                    plist.add(
                        db.collection(GAMES).document(id).collection(DRAWPILE).add(drawpile[i])
                    )
                }

                Tasks.whenAll(plist).continueWith { id }
            }
        }
    }

    /**
     * Given a [gameId] the matching game will be returned from the database.
     */
    fun getGame(gameId: String): DocumentReference {

        return db.collection("games").document(gameId)
    }

    /**
     * Adds an user with its [userId] and [userName ]to an existing game via a provided [gameId].
     */
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

    /**
     * For a list of [players] the necessary roles (loyalist, imperialist, sith) for the game are distributed.
     */
    private fun generatePlayerRolesAndOrder(players: List<Player>): List<Player> {
        val playerRoleList: Map<Int, PlayerRole>

        if (players.size == 5) {
            playerRoleList = mapOf(
                4 to PlayerRole.loyalist,
                2 to PlayerRole.loyalist,
                1 to PlayerRole.loyalist,
                3 to PlayerRole.imperialist,
                0 to PlayerRole.sith
            )
        } else {
            throw Exception("Number of players is not supported.")
        }

        val shuffledKeys = playerRoleList.keys.shuffled()
        var players = players.shuffled()
        for (key in shuffledKeys) {
            players[key].role = playerRoleList[key] ?: error("")
            players[key].order = key
        }

        return players
    }

    /**
     * Returns a fitting reference for a player in a game identified by [gameId] and [playerId] only-
     */
    private fun buildPlayerRef(gameId: String, playerId: String): DocumentReference {
        return db.collection("games").document(gameId).collection("players").document(playerId)
    }
}