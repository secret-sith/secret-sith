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

    /**
     * The Player of this client.
     */
    private lateinit var playerRef: Query
    private var playerReg: ListenerRegistration? = null
    private val _player = MutableLiveData<Player>()
    val player: LiveData<Player> = _player

    private lateinit var playersRef: Query
    private var playersReg: ListenerRegistration? = null
    private val _players = MutableLiveData<List<Player>>()
    val players: LiveData<List<Player>> = _players

    private lateinit var currentHandRef: Query
    private var currentHandReg: ListenerRegistration? = null
    private val _currentHand = MutableLiveData<List<Policy>>()
    val currentHand: LiveData<List<Policy>> = _currentHand

    private lateinit var drawpileRef: Query
    private var drawpileReg: ListenerRegistration? = null
    private val _drawpile = MutableLiveData<List<Policy>>()

    private val _endGameResult = MutableLiveData<EndGameResult>()
    val endGameResult: LiveData<EndGameResult> = _endGameResult

    fun setGameId(gameId: String) {
        gameRef = Firebase.firestore.collection(GAMES).document(gameId)
        gameReg?.remove()
        gameReg = gameRef.addSnapshotListener { snapshot, exception ->
            _game.value = snapshot?.toObject(Game::class.java)
            if (exception != null) Log.e(null, null, exception)
        }

        playerRef = gameRef.collection(PLAYERS).whereEqualTo(USER, auth.currentUser!!.uid).limit(1)
        playerReg?.remove()
        playerReg = playerRef.addSnapshotListener { snapshot, exception ->
            _player.value = snapshot?.documents?.getOrNull(0)?.toObject(Player::class.java)
            if (exception != null) Log.e(null, null, exception)
        }

        playersRef = gameRef.collection(PLAYERS).orderBy(ORDER)
        playersReg?.remove()
        playersReg = playersRef.addSnapshotListener { snapshot, exception ->
            _players.value = snapshot?.toObjects(Player::class.java)
            if (exception != null) Log.e(null, null, exception)
        }

        currentHandRef = gameRef.collection(CURRENTHAND).orderBy(ORDER)
        currentHandReg?.remove()
        currentHandReg = currentHandRef.addSnapshotListener { snapshot, exception ->
            _currentHand.value = snapshot?.toObjects(Policy::class.java)
            if (exception != null) Log.e(null, null, exception)
        }

        drawpileRef = gameRef.collection(DRAWPILE).orderBy(ORDER)
        drawpileReg?.remove()
        drawpileReg = drawpileRef.addSnapshotListener { snapshot, exception ->
            _drawpile.value = snapshot?.toObjects(Policy::class.java)
            if (exception != null) Log.e(null, null, exception)
        }
    }

    /**
     * The GamePhase representing the Fragment shown in this client, or null, if this client does
     * not participate in the current GamePhase.
     */
    val activeGamePhase = MutableLiveData<GamePhase>()

    init {
        var oldGamePhase: GamePhase? = null
        game.observeForever { game ->
            val player = player.value
            oldGamePhase = updateActiveGamePhase(game, player, oldGamePhase)
        }
        player.observeForever { player ->
            val game = game.value
            oldGamePhase = updateActiveGamePhase(game, player, oldGamePhase)
        }
    }

    private fun updateActiveGamePhase(
        game: Game?, player: Player?, oldGamePhase: GamePhase?
    ): GamePhase? {
        val newGamePhase = getActiveGamePhase(game, player)
        if (newGamePhase != oldGamePhase) activeGamePhase.value = newGamePhase
        return newGamePhase
    }

    private fun getActiveGamePhase(game: Game?, player: Player?): GamePhase? {
        if (game == null) return null
        if (player == null) return null
        val currentPlayerParticipates = when (game.phase) {
            GamePhase.vote -> player.killed != true
            GamePhase.nominate_chancellor -> game.presidentialCandidate?.id == player.id
            GamePhase.president_discard_policy -> game.president?.id == player.id
            GamePhase.chancellor_discard_policy -> game.chancellor?.id == player.id
            GamePhase.policy_peek -> game.president?.id == player.id
            GamePhase.kill -> game.president?.id == player.id
            GamePhase.president_accept_veto -> game.president?.id == player.id
            else -> false

        }
        if (player.killed != null && player.killed!!) return GamePhase.killed
        if (!currentPlayerParticipates) return null
        return game.phase
    }

    fun setChancellorCandidate(player: Player): Task<Void> {
        val playerRef = getPlayerRef(player.id)
        return gameRef.update(CHANCELLORCANDIDATE, playerRef).onSuccessTask {
            setGamePhase(GamePhase.vote)
        }
    }

    fun killPlayer(player: Player): Task<Void> {
        val playerRef = getPlayerRef(player.id)
        return playerRef.update(KILLED, true).onSuccessTask {
            setGamePhase(GamePhase.nominate_chancellor)
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
            GamePhase.vote -> {
                playersRef.get().onSuccessTask { snapshot ->
                    Tasks.whenAll(snapshot!!.documents.map { it.reference.update(VOTE, null) })
                }
            }
            GamePhase.president_discard_policy -> {
                val currentHand = currentHand.value!!
                if (currentHand.isEmpty()) drawCards(3)
                else Tasks.forResult(Unit)
            }
            GamePhase.policy_peek -> drawCards(3)
            else -> Tasks.forResult(Unit)
        }
        return task.onSuccessTask {
            gameRef.update(PHASE, phase)
        }
    }

    private fun drawCards(amount: Long): Task<Void> {
        return drawpileRef.limit(amount).get().onSuccessTask {
            Tasks.whenAll(it!!.documents.flatMap { snapshot ->
                val policy = snapshot.data!!
                val deleteTask = snapshot.reference.delete()
                val addTask = gameRef.collection(CURRENTHAND).add(policy)
                listOf(deleteTask, addTask)
            })
        }
    }

    private fun getNextPresidentialCandidate(game: Game, players: List<Player>): DocumentReference {
        val playersAlive = players.filter { it.killed != true }
        val oldRef = game.presidentialCandidate
        val oldIndex = playersAlive.indexOfFirst { it.id == oldRef?.id }
        val newIndex = (oldIndex + 1) % playersAlive.size
        val newPresidentialCandidate = playersAlive[newIndex]
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
        var yesVotes = 0
        var noVotes = 0

        val players = players.value!!
        val playersAlive = players.filter { it.killed != true }
        for (player in playersAlive) {
            if (player.vote!!) {
                yesVotes++
            } else {
                noVotes++
            }
        }

        if (yesVotes > noVotes) {
            val game = game.value!!
            gameRef.update(
                mapOf(
                    PRESIDENT to game.presidentialCandidate,
                    CHANCELLOR to game.chancellorCandidate
                )
            ).onSuccessTask {
                setGamePhase(GamePhase.president_discard_policy)
            }
        } else {
            onGovernmentFailed()
        }
    }

    fun veto() {
        currentHandRef.get().onSuccessTask { snapshot ->
            Tasks.whenAll(snapshot!!.documents.map { it.reference.delete() })
        }.onSuccessTask {
            onGovernmentFailed()
        }
    }

    private fun onGovernmentFailed(): Task<Void> {
        return gameRef.update(FAILEDGOVERNMENTS, FieldValue.increment(1)).onSuccessTask {
            gameRef.get()
        }.onSuccessTask {
            val game = it!!.toObject(Game::class.java)!!
            if (game.failedGovernments == 3) {
                handleChaos()
            } else {
                Tasks.forResult<Void>(null)
            }
        }.onSuccessTask {
            setGamePhase(GamePhase.nominate_chancellor)
        }
    }

    private fun handleChaos(): Task<Void> {
        return drawpileRef.limit(1).get().onSuccessTask {
            val snapshot = it!!.documents[0]
            val deleteTask = snapshot.reference.delete()
            val policy = snapshot.toObject(Policy::class.java)!!
            val policyTask = enactPolicy(policy, isChaosPolicy = true)
            Tasks.whenAll(deleteTask, policyTask)
        }.onSuccessTask {
            gameRef.update(
                mapOf(
                    CHANCELLOR to null,
                    PRESIDENT to null,
                    FAILEDGOVERNMENTS to 0
                )
            )
        }
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

    fun presidentDiscardPolicy(i: Int): Task<Void> {
        val currentHand = currentHand.value!!
        val discardedPolicy = currentHand[i]
        return gameRef.collection(CURRENTHAND).document(discardedPolicy.id).delete().onSuccessTask {
            setGamePhase(GamePhase.chancellor_discard_policy)
        }
    }

    fun chancellorDiscardPolicy(i: Int): Task<Void> {
        val currentHand = currentHand.value!!
        val enactedPolicy = currentHand[1 - i]
        val currentHandRefs = currentHand.map { gameRef.collection(CURRENTHAND).document(it.id) }
        return Tasks.whenAll(currentHandRefs.map { it.delete() }).onSuccessTask {
            enactPolicy(enactedPolicy)
        }
    }

    private fun enactPolicy(policy: Policy, isChaosPolicy: Boolean = false): Task<Void> {
        val game = game.value!!

        val policyField =
            if (policy.type == PolicyType.imperialist) IMPERIALPOLITICS else LOYALISTPOLITICS
        val updateGameTask = gameRef.update(
            mapOf(
                policyField to FieldValue.increment(1),
                FAILEDGOVERNMENTS to 0
            )
        )

        var nextPhase = GamePhase.nominate_chancellor
        if (policy.type == PolicyType.imperialist && !isChaosPolicy) {
            when (game.imperialPolitics + 1) {
                3 -> nextPhase = GamePhase.policy_peek
                4 -> nextPhase = GamePhase.kill
                5 -> nextPhase = GamePhase.kill
            }
        }

        return updateGameTask.onSuccessTask {
            refreshDrawpileIfNeccessary()
        }.onSuccessTask {
            setGamePhase(nextPhase)
        }
    }

    private fun refreshDrawpileIfNeccessary(): Task<Void> {
        return gameRef.collection(DRAWPILE).get().onSuccessTask { drawpile ->
            if (drawpile == null || drawpile.size() >= 3) {
                return@onSuccessTask Tasks.forResult<Void>(null)
            }
            Tasks.whenAll(drawpile.documents.map { it.reference.delete() }).onSuccessTask {
                gameRef.get()
            }.onSuccessTask {
                val game = it!!.toObject(Game::class.java)!!
                val imperialPolicyCount = 11 - game.imperialPolitics
                val loyalistPolicyCount = 6 - game.loyalistPolitics
                val imperialistPolicies = (1..imperialPolicyCount).map { PolicyType.imperialist }
                val loyalistPolicies = (1..loyalistPolicyCount).map { PolicyType.loyalist }
                val policies = (imperialistPolicies + loyalistPolicies).shuffled()
                Tasks.whenAll(policies.mapIndexed { index, policyType ->
                    gameRef.collection(DRAWPILE).add(
                        mapOf(
                            TYPE to policyType,
                            ORDER to index
                        )
                    )
                })
            }
        }
    }

    fun loadGameAndPlayersForPendingState(gameId: String) {
        gameRepository.getGame(gameId).addSnapshotListener { snapshot, exception ->
            val game = snapshot?.toObject(Game::class.java)
            _game.value = game
            if (exception != null) Log.e(null, null, exception)
        }

        gameRepository.getPlayers(gameId).addSnapshotListener { snapshot, exception ->
            if (snapshot != null) {
                _players.value = snapshot.toObjects(Player::class.java)
            }
            if (exception != null) Log.e(null, null, exception)
        }
    }

    fun canStartGame(): Boolean {
        val players = players.value
        if (players == null) return false
        if (players.size != 5) return false
        for (player in players) {
            if (player.state != PlayerState.accepted) return false
        }
        return true
    }

    fun getCurrentUsersGameId(): Task<String?> {
        return userRepository.getLiveUser(auth.currentUser!!.uid).get().continueWith {
            val snapshot = it.result
            val user = snapshot?.toObject(User::class.java)
            user?.currentGame
        }
    }
}