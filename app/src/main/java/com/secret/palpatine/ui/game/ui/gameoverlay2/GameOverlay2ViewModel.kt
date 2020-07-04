package com.secret.palpatine.ui.game.ui.gameoverlay2

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.secret.palpatine.data.model.game.Game
import com.secret.palpatine.data.model.game.GamePhase
import com.secret.palpatine.data.model.game.Policy
import com.secret.palpatine.data.model.player.Player
import com.secret.palpatine.util.*

class GameOverlay2ViewModel : ViewModel() {
    private lateinit var gameRef: DocumentReference
    private var gameReg: ListenerRegistration? = null
    private val _game = MutableLiveData<Game>()
    val game: LiveData<Game> = _game

    var userId: String? = null
    var userName: String? = null
    var thisPlayer: Player? = null

    private lateinit var playersRef: Query
    private var playersReg: ListenerRegistration? = null
    private val _players = MutableLiveData<List<Player>>()
    val players: LiveData<List<Player>> = _players

    private lateinit var currentHandRef: Query
    private var currentHandReg: ListenerRegistration? = null
    private val _currentHand = MutableLiveData<List<Policy>>()
    val currentHand: LiveData<List<Policy>> = _currentHand

    fun setGameId(gameId: String) {
        gameRef = Firebase.firestore.collection("games").document(gameId)
        gameReg?.remove()
        gameReg = gameRef.addSnapshotListener { snapshot, exception ->
            val game = snapshot?.toObject(Game::class.java)
            _game.value = game
            if (exception != null) Log.e(null, null, exception)
        }
        playersRef = gameRef.collection("players").orderBy("index")
        playersReg?.remove()
        playersReg = playersRef.addSnapshotListener { snapshot, exception ->
            if (snapshot != null) {
                _players.value = snapshot.toObjects(Player::class.java)
                for (player in players.value!!) {
                    if (userId == player.user) {
                        thisPlayer = player
                    }
                }
            }
            if (exception != null) Log.e(null, null, exception)
        }
        currentHandRef = gameRef.collection("currentHand").orderBy("index")
        currentHandReg?.remove()
        currentHandReg = currentHandRef.addSnapshotListener { snapshot, exception ->
            if (snapshot != null) {
                _currentHand.value = snapshot.toObjects(Policy::class.java)
            }
            if (exception != null) Log.e(null, null, exception)
        }
    }

    fun setChancellorCandidate(player: Player) {
        val playerRef = getPlayerRef(player.id)
        gameRef.update(
            mapOf(
                "chancellorCandidate" to playerRef,
                "phase" to GamePhase.vote
            )
        )
    }

    // TODO: This should be a trigger when phase is set to nominate_chancellor
    fun nominateNextPresident() {
        val game = game.value!!
        val players = players.value!!
        game.presidentialCandidate!!.get().addOnSuccessListener {
            val presidentialCandidate = it.toObject(Player::class.java)!!
            val nextPresidentialCandidate = players[presidentialCandidate.index + 1 % players.size]
            val nextPresidentialCandidateRef = getPlayerRef(nextPresidentialCandidate.id)
            gameRef.update(
                mapOf(
                    "presidentialCandidate" to nextPresidentialCandidateRef,
                    "chancellorCandidate" to null,
                    "phase" to GamePhase.nominate_chancellor
                )
            )
        }
    }


    fun handleElectionResult() {

        // check the election result
        var yesVotes = 0
        var noVotes = 0
        for (player in players.value!!) {
            if (player.vote!!) {
                yesVotes++
            } else {
                noVotes++
            }
        }

        // case election was successful
        if (yesVotes > noVotes) {

            // save the new elects and advance the game phase
            gameRef.update(
                mapOf(
                    PRESIDENT to _game.value!!.presidentialCandidate,
                    CHANCELLOR to _game.value!!.chancellorCandidate,
                    PRESIDENTIALCANDIDATE to null,
                    CHANCELLORCANDIDATE to null,
                    PHASE to GamePhase.president_discard_politic,
                    FAILEDGOVERNMENTS to 0
                )
            )
        }

        // case election failed
        if (yesVotes <= noVotes) {

            // update the counter for failed governments and set the new presidential candidate
            gameRef.update(
                mapOf(
                    FAILEDGOVERNMENTS to FieldValue.increment(1),
                    PRESIDENTIALCANDIDATE to getNextPresidentialCandidate(),
                    CHANCELLORCANDIDATE to null,
                    PHASE to GamePhase.nominate_chancellor
                )

            )
                .addOnSuccessListener {
                    // check if country is thrown into chaos
                    if (game.value!!.failedGovernments == 3) {
                        // play the next politic from the pile but ignore executive powers that would take place
                        // shuffle the discard pile into the draw pile in case there are only two cards left
                        // TODO play next politic from pile + check if not enough cards
                        gameRef.update(
                            mapOf(
                                CHANCELLOR to null,
                                FAILEDGOVERNMENTS to 0
                            )
                        )
                    }

                }

        }
        for (player in players.value!!) {
            getPlayerRef(player.id).update(VOTE, null)
        }

    }

    fun getNextPresidentialCandidate(): DocumentReference? {
        val presidentID = game.value!!.president!!.id
        val playerList = players.value!!

        for (i in 0 until players.value!!.size) {
            if (playerList[i].id == presidentID) {
                val nextPresidentPosition: Int = (i + 1) % playerList.size
                val nextPresidentId = playerList[nextPresidentPosition].id
                return getPlayerRef(nextPresidentId)
            }
        }
        return null
    }

    fun getPlayerForReference(playerReference: DocumentReference): Player? {
        for (player in players.value!!) {
            if (player.id == playerReference.id) {
                return player
            }
        }
        return null
    }

    fun getPlayerRef(playerId: String): DocumentReference {
        return gameRef.collection("players").document(playerId)
    }

}
