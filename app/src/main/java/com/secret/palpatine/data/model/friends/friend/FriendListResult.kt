package com.secret.palpatine.data.model.friends.friend

import com.secret.palpatine.data.model.user.User


/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
data class FriendListResult(
    val success: List<User>? = null,
    val error: Int? = null
)