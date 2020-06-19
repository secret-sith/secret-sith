package com.secret.palpatine.data.model.friends.friend.request

import com.secret.palpatine.data.model.friends.friend.request.FriendRequest

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
data class FriendRequestListResult(
    val success: List<FriendRequest>? = null,
    val error: Int? = null
)