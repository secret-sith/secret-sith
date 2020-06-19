package com.secret.palpatine.data.model.game

import com.secret.palpatine.data.model.friends.friend.request.FriendRequest

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
data class CreateGameResult(
    val success: Boolean = false,
    val gameId: String? = null,
    val error: Int? = null
)