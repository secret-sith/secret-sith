package com.secret.palpatine.ui.signup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.secret.palpatine.data.login.LoginRepository
import com.secret.palpatine.data.login.Result

import com.secret.palpatine.R

class SignupViewModel(private val firebaseAuth: FirebaseAuth) : ViewModel() {


    private val user = firebaseAuth.currentUser

    private val _loginForm = MutableLiveData<SignupFormState>()
    val signupFormState: LiveData<SignupFormState> = _loginForm

    private val _signupResult = MutableLiveData<SignupResult>()
    val signupResult: LiveData<SignupResult> = _signupResult

    fun signup(username: String, password: String, name: String) {

        firebaseAuth.createUserWithEmailAndPassword(username, password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = firebaseAuth.currentUser
                    val profileUpdates = userProfileChangeRequest {
                        displayName = name
                    }
                    user!!.updateProfile(profileUpdates)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                _signupResult.value =
                                    SignupResult(success = user)
                            }
                        }


                } else {
                    _signupResult.value = SignupResult(error = R.string.signup_failed)
                }

            }
    }

    fun signUpDataChanged(email: String, password: String, name: String) {

        if (!isUserNameValid(email)) {
            _loginForm.value = SignupFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = SignupFormState(passwordError = R.string.invalid_password)
        } else if (!isNameValid(name)) {
            _loginForm.value = SignupFormState(nameError = R.string.invalide_name)
        } else {
            _loginForm.value = SignupFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    private fun isNameValid(name: String): Boolean {
        return name.isNotBlank()
    }
}
