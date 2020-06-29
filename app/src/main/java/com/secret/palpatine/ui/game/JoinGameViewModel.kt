package com.secret.palpatine.ui.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.secret.palpatine.R
import com.secret.palpatine.data.model.game.GameRepository
import com.secret.palpatine.data.model.user.CurrentUserResult
import com.secret.palpatine.data.model.user.User
import com.secret.palpatine.data.model.user.UserRepository

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

        gameRepository.joinGame(gameId, auth.currentUser!!.uid, username).addOnSuccessListener {
            _joinGameResult.value = JoinGameResult(success = true)
        }.addOnFailureListener {
            _joinGameResult.value = JoinGameResult(success = false, error = 1)
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