package com.secret.palpatine.ui.signup

import com.google.firebase.auth.FirebaseUser

/**
 * Authentication result : success (user details) or error message.
 */
data class SignupResult(
    val success: FirebaseUser? = null,
    val error: Int? = null
)
