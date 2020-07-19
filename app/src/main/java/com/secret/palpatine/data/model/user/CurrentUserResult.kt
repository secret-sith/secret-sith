package com.secret.palpatine.data.model.user

/**
 * Contains a user if underlying query was successful.
 * @property user User object.
 * @property error Corresponding errors
 */
data class CurrentUserResult(
    val user: User? = null,
    val error: Int? = null
)