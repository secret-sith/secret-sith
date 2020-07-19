package com.secret.palpatine.ui.game

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.secret.palpatine.R
import com.secret.palpatine.data.model.game.GameRepository
import com.secret.palpatine.data.model.user.CurrentUserResult
import com.secret.palpatine.data.model.user.User
import com.secret.palpatine.data.model.user.UserRepository
import com.secret.palpatine.util.*

/**
 * Created by Florian Fuchs on 22.06.2020.
 */
class JoinGameViewModel(
    private val auth: FirebaseAuth,
    private val gameRepository: GameRepository,
    private val userRepository: UserRepository
) : ViewModel() {


    private val _formResult = MutableLiveData<JoinGameFormState>()
    val formResult: LiveData<JoinGameFormState> = _formResult

    private val _joinGameResult = MutableLiveData<JoinGameResult>()
    val joinGameResult: LiveData<JoinGameResult> = _joinGameResult

    private val _currentUserResult = MutableLiveData<CurrentUserResult>()
    val currentUserResult: LiveData<CurrentUserResult> = _currentUserResult


    fun formDataChange(name: String) {
        if (!isNameValid(name)) {
            _formResult.value = JoinGameFormState(usernameError = R.string.invalide_name)
        } else {
            _formResult.value = JoinGameFormState(isDataValid = true)
        }
    }

    fun joinGame(gameId: String, username: String) {
        val db = Firebase.firestore
        val userId = auth.currentUser!!.uid
        var alreadyJoined = false
        db.collection(GAMES).document(gameId).collection(PLAYERS).whereEqualTo(USER, userId).get()
            .addOnSuccessListener { documents ->
                Log.v("DEBUG", "1. " + documents.toString())
                for (player in documents) {
                    Log.v("DEBUG", "2. " + player.toString())
                    db.collection(GAMES).document(gameId).collection(PLAYERS).document(player.id)
                        .update(STATE, "accepted")
                    alreadyJoined = true
                }
                Log.v("DEBUG", "3. alreadyJoined => " + alreadyJoined)
                if (alreadyJoined) {
                    val userUpdate = hashMapOf("currentGame" to gameId)
                    db.collection(USERS).document(userId).set(userUpdate, SetOptions.merge())
                        .addOnSuccessListener {
                            _joinGameResult.value = JoinGameResult(success = true)
                        }
                    return@addOnSuccessListener
                }
                gameRepository.joinGame(gameId, auth.currentUser!!.uid, username)
                    .addOnSuccessListener {
                        _joinGameResult.value = JoinGameResult(success = true)
                    }.addOnFailureListener {
                    _joinGameResult.value = JoinGameResult(success = false, error = 1)
                }
            }
    }

    fun getCurrentUser() {
        userRepository.getUserByReference(auth.currentUser!!.uid).addOnSuccessListener {
            var user = it.toObject(User::class.java)
            _currentUserResult.value = CurrentUserResult(user = user)
        }.addOnFailureListener {
            _currentUserResult.value = CurrentUserResult(error = 1)
        }
    }

    private fun isNameValid(name: String): Boolean {
        return name.isNotBlank()
    }
}