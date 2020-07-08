package com.secret.palpatine.ui.game

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.secret.palpatine.data.model.game.*
import com.secret.palpatine.data.model.player.Player
import com.secret.palpatine.data.model.player.PlayerState
import com.secret.palpatine.data.model.user.User
import com.secret.palpatine.data.model.user.UserRepository
import com.secret.palpatine.util.*

/**
 * Created by Florian Fuchs on 19.06.2020.
 */
class GameViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val gameRepository: GameRepository = GameRepository()
    private val userRepository: UserRepository = UserRepository()

    private lateinit var gameRef: DocumentReference
    private var gameReg: ListenerRegistration? = null
    private val _game = MutableLiveData<Game>()
    val game: LiveData<Game> = _game

    val gamePhase: LiveData<GamePhase>

    init {
        val _gamePhase = MutableLiveData<GamePhase>()
        game.observeForever {
            _gamePhase.value = it.phase
        }
        gamePhase = _gamePhase
    }

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

    private val _endGameResult = MutableLiveData<EndGameResult>()
    val endGameResult: LiveData<EndGameResult> = _endGameResult

    fun setGameId(gameId: String) {
        gameRef = Firebase.firestore.collection(GAMES).document(gameId)
        gameReg?.remove()
        gameReg = gameRef.addSnapshotListener { snapshot, exception ->
            val game = snapshot?.toObject(Game::class.java)
            _game.value = game
            if (exception != null) Log.e(null, null, exception)
        }
        playersRef = gameRef.collection(PLAYERS).orderBy(ORDER)
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
        currentHandRef = gameRef.collection(CURRENTHAND).orderBy(ORDER)
        currentHandReg?.remove()
        currentHandReg = currentHandRef.addSnapshotListener { snapshot, exception ->
            if (snapshot != null) {
                _currentHand.value = snapshot.toObjects(Policy::class.java)
            }
            if (exception != null) Log.e(null, null, exception)
        }
    }

    fun setChancellorCandidate(player: Player): Task<Void> {
        val playerRef = getPlayerRef(player.id)
        return gameRef.update(CHANCELLORCANDIDATE, playerRef).continueWithTask {
            setGamePhase(GamePhase.vote)
        }
    }

    fun setGamePhase(phase: GamePhase): Task<Void> {
        val game = game.value!!
        if (phase == game.phase) return Tasks.forResult(null)
        val task = when (phase) {
            GamePhase.nominate_chancellor -> {
                val players = players.value!!
                val newPresidentialCandidateRef = getNextPresidentialCandidate(game, players)
                gameRef.update(
                    mapOf(
                        PRESIDENTIALCANDIDATE to newPresidentialCandidateRef,
                        CHANCELLORCANDIDATE to null
                    )
                )
            }
            GamePhase.policy_peek -> {
                gameRef.collection(CURRENTHAND).get().continueWithTask { task ->
                    Tasks.whenAll(task.result!!.documents.map { it.reference.delete() })
                }.continueWithTask {
                    gameRef.collection(DRAWPILE).orderBy(ORDER).limit(3).get()
                }.continueWithTask { task ->
                    Tasks.whenAll(task.result!!.documents.flatMap {
                        val policy = it.data!!
                        val deleteTask = it.reference.delete()
                        val addTask = gameRef.collection(CURRENTHAND).add(policy)
                        listOf(deleteTask, addTask)
                    })
                }
            }
            else -> Tasks.forResult(Unit)
        }
        return task.continueWithTask {
            gameRef.update(PHASE, phase)
        }
    }

    private fun getNextPresidentialCandidate(game: Game, players: List<Player>): DocumentReference {
        val oldPresidentialCandidateRef = game.presidentialCandidate
        val oldIndex =
            if (oldPresidentialCandidateRef != null) players.indexOfFirst { it -> it.id == oldPresidentialCandidateRef.id }
            else -1
        val newIndex = (oldIndex + 1) % players.size
        val newPresidentialCandidate = players[newIndex]
        return getPlayerRef(newPresidentialCandidate.id)
    }

    fun start() {
        gameRepository.startGame(game.value!!.id, players.value!!).addOnSuccessListener {
            Log.d("Game", "Game was started")
        }.addOnFailureListener {
            Log.e("Game", "Error while starting game")
        }
    }

    fun endGame() {
        userRepository.endGameForPlayer(auth.currentUser!!.uid).addOnSuccessListener {

            _endGameResult.value = EndGameResult(success = true)
        }.addOnFailureListener {
            _endGameResult.value = EndGameResult(success = false)

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
                    PHASE to GamePhase.president_discard_policy,
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

    private fun checkCanStartGame(players: List<Player>): Boolean {
        if (players.size < 2) {
            return false
        } else {
            for (player in players) {
                if (player.state != PlayerState.accepted) {
                    return false
                }
            }
        }
        return true
    }

    fun discardPolicy(i: Int): Task<Void> {
        val currentHand = currentHand.value!!
        val discardedPolicy = currentHand[i]
        val discardedPolicyRef = gameRef.collection(CURRENTHAND).document(discardedPolicy.id)
        return discardedPolicyRef.delete().continueWithTask {
            if (currentHand.size == 2) {
                val enactedPolicy = currentHand[1 - i]
                enactPolicy(enactedPolicy).continueWithTask {
                    setGamePhase(GamePhase.nominate_chancellor)
                }
            } else {
                setGamePhase(GamePhase.chancellor_discard_policy)
            }
        }
    }

    fun loadGameAndPlayersForPendingState(gameId: String){

        gameRepository.getGame(gameId).addSnapshotListener { snapshot, exception ->
            val game = snapshot?.toObject(Game::class.java)
            _game.value = game
            if (exception != null) Log.e(null, null, exception)
        }

        gameRepository.getPlayers(gameId).addSnapshotListener { snapshot, exception ->
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
    }

    fun getCurrentUsersGameId(): Task<String?> {
        return userRepository.getLiveUser(auth.currentUser!!.uid).get().continueWith {
            val snapshot = it.result
            val user = snapshot?.toObject(User::class.java)
            user?.currentGame
        }
    }

    private fun enactPolicy(policy: Policy): Task<Void> {
        return if (policy.type == PolicyType.loyalist) {
            gameRef.update(LOYALISTPOLITICS, FieldValue.increment(1))
        } else {
            gameRef.update(IMPERIALPOLITICS, FieldValue.increment(1))
        }
    }
}