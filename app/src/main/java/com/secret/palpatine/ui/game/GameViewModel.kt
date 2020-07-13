package com.secret.palpatine.ui.game

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
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
            updateRoleOwners {
                _gamePhase.value = it.phase
            }

        }
        gamePhase = _gamePhase
    }

    var userId: String? = null
    var userName: String? = null
    var thisPlayer: Player? = null
    var presidentialCandidate: Player? = null
    var chancellorCandidate: Player? = null
    var chancellor: Player? = null
    var president: Player? = null
    var oldGamePhase: GamePhase? = null

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
    val drawpile: LiveData<List<Policy>> = _drawpile

    private val _endGameResult = MutableLiveData<EndGameResult>()
    val endGameResult: LiveData<EndGameResult> = _endGameResult



    fun setGameId(gameId: String) {
        gameRef = Firebase.firestore.collection(GAMES).document(gameId)
        gameReg?.remove()
        gameReg = gameRef.addSnapshotListener { snapshot, exception ->
            val game = snapshot?.toObject(Game::class.java)
            if (oldGamePhase != game?.phase){
                updateRoleOwners(){
                    _game.value = game
                }
            } else {
                _game.value = game
            }
            oldGamePhase = game?.phase
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

        drawpileRef = gameRef.collection(DRAWPILE).orderBy(ORDER)
        drawpileReg?.remove()
        drawpileReg = drawpileRef.addSnapshotListener { snapshot, exception ->
            if(snapshot != null){
                _drawpile.value = snapshot.toObjects(Policy::class.java)
            }
            if (exception != null) Log.e(null,null, exception)
        }

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
            GamePhase.president_discard_policy -> {
                val currentHand = currentHand.value!!
                drawCards(3L - currentHand.size)
            }
            GamePhase.policy_peek -> drawCards(3)
            else -> Tasks.forResult(Unit)
        }
        return task.onSuccessTask {
            gameRef.update(PHASE, phase)
        }
    }

    private fun drawCards(amount: Long): Task<Void> {
        return gameRef.collection(DRAWPILE).orderBy(ORDER).limit(amount).get().onSuccessTask {
            Tasks.whenAll(it!!.documents.flatMap { snapshot ->
                val policy = snapshot.data!!
                val deleteTask = snapshot.reference.delete()
                val addTask = gameRef.collection(CURRENTHAND).add(policy)
                listOf(deleteTask, addTask)
            })
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
        var yesVotes = 0; var noVotes = 0

        for (player in players.value!!) {
            if (player.vote!!) { yesVotes++ }
            else { noVotes++ }
        }

        // case election was successful
        if (yesVotes > noVotes) {
            // save the new elects and advance the game phase
            gameRef.update(
                mapOf(
                    PHASE to GamePhase.president_discard_policy,
                    PRESIDENT to _game.value!!.presidentialCandidate,
                    CHANCELLOR to _game.value!!.chancellorCandidate,
                    PRESIDENTIALCANDIDATE to null,
                    CHANCELLORCANDIDATE to null,
                    FAILEDGOVERNMENTS to 0
                )
            )
        }

        // case election failed
        if (yesVotes <= noVotes) {
            // update the counter for failed governments and set the new presidential candidate
            gameRef.update(
                mapOf(
                    PHASE to GamePhase.nominate_chancellor,
                    FAILEDGOVERNMENTS to FieldValue.increment(1),
                    PRESIDENTIALCANDIDATE to getNextPresidentialCandidate(),
                    CHANCELLORCANDIDATE to null
                )

            ).addOnSuccessListener {
                    // check if country is thrown into chaos
                    if (game.value!!.failedGovernments == 3) {
                        handleFailedGovernment()
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

    private fun enactPolicy(policy: Policy): Task<Void> {
        val game = game.value!!
        return if (policy.type == PolicyType.loyalist) {
            gameRef.update(LOYALISTPOLITICS, FieldValue.increment(1)).onSuccessTask {
                refreshDrawpileIfNeccessary()
            }.onSuccessTask {
                setGamePhase(GamePhase.nominate_chancellor)
            }
        } else {
            val imperialPolitics = game.imperialPolitics + 1
            val phase = when (imperialPolitics) {
                3 -> GamePhase.policy_peek
                4 -> GamePhase.kill
                5 -> GamePhase.kill
                else -> GamePhase.nominate_chancellor
            }
            gameRef.update(IMPERIALPOLITICS, imperialPolitics).onSuccessTask {
                refreshDrawpileIfNeccessary()
            }.onSuccessTask {
                setGamePhase(phase)
            }
        }
    }

    private fun refreshDrawpileIfNeccessary(): Task<Void> {
        return gameRef.collection(DRAWPILE).get().onSuccessTask { drawpile ->
            if (drawpile == null || drawpile.size() >= 3) return@onSuccessTask Tasks.forResult<Void>(
                null
            )
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


    private fun handleFailedGovernment(){
        var minOrder = Int.MAX_VALUE
        var nextCard: QueryDocumentSnapshot? = null


        drawpileRef.get().addOnSuccessListener { drawpile ->
            for (element in drawpile){
                if((element[ORDER].toString().toInt()) < minOrder){
                    minOrder = element[ORDER].toString().toInt()
                    nextCard = element
                }
            }

            val politic = nextCard?.get(TYPE).toString()
            if (politic == LOYALIST) {
                gameRef.update(LOYALISTPOLITICS, FieldValue.increment(1))
            } else {
                gameRef.update(IMPERIALPOLITICS, FieldValue.increment(1))
            }

            gameRef.collection(DRAWPILE).document(nextCard!!.id).delete()
            }

        gameRef.update(
            mapOf(
                FAILEDGOVERNMENTS to 0
            )
        )
    }


    private fun updateRoleOwners(callback: () -> Unit){
        val taskList: ArrayList<Task<DocumentSnapshot>?> = arrayListOf()
        taskList.add(game.value?.presidentialCandidate?.get()?.addOnSuccessListener {
            for (player in players.value!!){
                if (it[USER] == player.user){
                    presidentialCandidate = player
                    break
                }
            }
        })
        taskList.add(game.value?.chancellorCandidate?.get()?.addOnSuccessListener {
            for (player in players.value!!){
                if(it[USER] == player.user){
                    chancellorCandidate = player
                    break
                }
            }
        })
        taskList.add(game.value?.president?.get()?.addOnSuccessListener {
            for (player in players.value!!){
                if(it[USER] == player.user){
                    president = player
                    break
                }
            }
        })
        taskList.add(game.value?.chancellor?.get()?.addOnSuccessListener {
            for (player in players.value!!){
                if(it[USER] == player.user){
                    chancellor = player
                    break
                }
            }
        })
        Tasks.whenAll(taskList.filterNotNull()).continueWith { callback() }
    }
}