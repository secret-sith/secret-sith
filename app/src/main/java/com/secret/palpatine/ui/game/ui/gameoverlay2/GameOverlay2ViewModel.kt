package com.secret.palpatine.ui.game.ui.gameoverlay2

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.secret.palpatine.data.model.game.Game
import com.secret.palpatine.data.model.game.GamePhase
import com.secret.palpatine.data.model.player.Player

class GameOverlay2ViewModel : ViewModel() {
    private lateinit var gameRef: DocumentReference
    private var gameReg: ListenerRegistration? = null
    private val _game = MutableLiveData<Game>()
    val game: LiveData<Game> = _game

    private lateinit var playersRef: CollectionReference
    private var playersReg: ListenerRegistration? = null
    private val _players = MutableLiveData<List<Player>>()
    val players: LiveData<List<Player>> = _players

    fun setGameId(gameId: String) {
        gameRef = Firebase.firestore.collection("games").document(gameId)
        gameReg?.remove()
        gameReg = gameRef.addSnapshotListener(EventListener { snapshot, exception ->
            val game = snapshot?.toObject(Game::class.java)
            _game.value = game
            if (exception != null) Log.e(null, null, exception)
        })
        playersRef = gameRef.collection("players")
        playersReg?.remove()
        playersReg = playersRef.addSnapshotListener(EventListener { snapshot, exception ->
            _players.value = snapshot?.toObjects(Player::class.java)
            if (exception != null) Log.e(null, null, exception)
        })
    }

    fun setChancellorCandidate(player: Player) {
        val playerRef = playersRef.document(player.id)
        gameRef.update(
            mapOf(
                "chancellorCandidate" to playerRef,
                "phase" to GamePhase.vote
            )
        )
    }
}
