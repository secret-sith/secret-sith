package com.secret.palpatine.ui.signup

/**
 * Data validation state of the login form.
 */
data class SignupFormState(
    val nameError: Int? = null,
    val usernameError: Int? = null,
    val passwordError: Int? = null,
    val isDataValid: Boolean = false
)
