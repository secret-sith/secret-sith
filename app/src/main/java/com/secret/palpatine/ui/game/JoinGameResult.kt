package com.secret.palpatine.ui.game

import com.google.firebase.auth.FirebaseUser

/**
 * Authentication result : success (user details) or error message.
 */
data class JoinGameResult(
    val success: Boolean = false,
    val error: Int? = null
)
