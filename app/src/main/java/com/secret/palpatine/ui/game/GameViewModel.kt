package com.secret.palpatine.ui.game

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.toObject
import com.secret.palpatine.data.model.game.CurrentGameResult
import com.secret.palpatine.data.model.game.Game
import com.secret.palpatine.data.model.game.GameRepository
import com.secret.palpatine.data.model.player.Player
import com.secret.palpatine.data.model.player.PlayerState
import com.secret.palpatine.data.model.player.PlayersResult
import com.secret.palpatine.data.model.user.UserRepository

/**
 * Created by Florian Fuchs on 19.06.2020.
 */
class GameViewModel constructor(
    private val auth: FirebaseAuth,
    private val gameRepository: GameRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _currentGameResult = MutableLiveData<CurrentGameResult>()
    val currentGameResult: LiveData<CurrentGameResult> = _currentGameResult

    private val _playersResult = MutableLiveData<PlayersResult>()
    val playersResult: LiveData<PlayersResult> = _playersResult


    private val _canStartGame = MutableLiveData<Boolean>()
    val canStartGame: LiveData<Boolean> = _canStartGame


    fun getPlayers(gameId: String?) {

        if (gameId != null) {
            gameRepository.getPlayers(gameId).addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("GAME", "Listen failed.", e)
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty()) {

                    var playerList: MutableList<Player> = mutableListOf()
                    for (document in snapshot!!.documents) {
                        playerList.add(document.toObject<Player>()!!)
                    }
                    _canStartGame.value = checkCanStartGame(playerList)
                    _playersResult.value = PlayersResult(players = playerList)
                } else {
                    _playersResult.value = PlayersResult(error = 1)
                }
            }
        }
    }

    fun start() {


    }

    fun getGame(gameId: String?) {
        if (gameId != null) {
            gameRepository.getGame(gameId).addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("GAME", "Listen failed.", e)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {

                    val game = snapshot!!.toObject<Game>()
                    _currentGameResult.value = CurrentGameResult(game = game, gameId = gameId)
                } else {
                    _currentGameResult.value = CurrentGameResult(error = 1)
                }

            }
        } else {

            userRepository.getUserByReference(auth.currentUser!!.uid)
                .addOnSuccessListener { it ->
                    val currentGameId = it.getString("currentGame")
                    if (currentGameId != null) {
                        gameRepository.getGame(currentGameId)
                            .addSnapshotListener { snapshot, e ->
                                if (e != null) {
                                    Log.w("GAME", "Listen failed.", e)
                                    return@addSnapshotListener
                                }

                                if (snapshot != null && snapshot.exists()) {
                                    val game = snapshot!!.toObject<Game>()
                                    _currentGameResult.value =
                                        CurrentGameResult(game = game, gameId = currentGameId)
                                } else {
                                    _currentGameResult.value = CurrentGameResult(error = 1)
                                }

                            }

                    } else {
                        _currentGameResult.value = CurrentGameResult(error = 1)
                    }
                }.addOnFailureListener {
                    _currentGameResult.value = CurrentGameResult(error = 1)

                }

        }


    }

    private fun checkCanStartGame(players: List<Player>): Boolean {
        if (players.size < 4) {
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

}