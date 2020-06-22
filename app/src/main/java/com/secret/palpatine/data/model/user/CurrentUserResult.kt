package com.secret.palpatine.data.model.user

import com.secret.palpatine.data.model.friends.friend.request.FriendRequest

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
data class CurrentUserResult(
    val user: User? = null,
    val error: Int? = null
)