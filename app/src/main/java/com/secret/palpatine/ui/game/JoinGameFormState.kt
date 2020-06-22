package com.secret.palpatine.ui.game

/**
 * Data validation state of the Join game form.
 */
data class JoinGameFormState(
    val usernameError: Int? = null,
    val isDataValid: Boolean = false
)
