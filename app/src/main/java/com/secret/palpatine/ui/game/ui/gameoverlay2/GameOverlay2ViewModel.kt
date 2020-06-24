package com.secret.palpatine.ui.game.ui.gameoverlay2

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.secret.palpatine.data.model.game.Game
import com.secret.palpatine.data.model.player.Player

class GameOverlay2ViewModel : ViewModel() {
    private val _game = MutableLiveData<Game>()
    val game: LiveData<Game> = _game

    private val _players = MutableLiveData<List<Player>>()
    val players: LiveData<List<Player>> = _players

    fun setGameId(gameId: String)  {
        val gameRef = Firebase.firestore.collection("games").document(gameId)
        gameRef.addSnapshotListener(EventListener { snapshot, exception ->
            _game.value = snapshot?.toObject<Game>()
            if (exception != null) Log.e(null, null, exception)
        })
        val playersRef = Firebase.firestore.collection("games").document(gameId).collection("players")
        playersRef.addSnapshotListener(EventListener { snapshot, exception ->
            _players.value = snapshot?.toObjects(Player::class.java)
            if (exception != null) Log.e(null, null, exception)
        })
    }
}
