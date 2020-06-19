package com.secret.palpatine.data.model.game

import com.secret.palpatine.data.model.friends.friend.request.FriendRequest

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
data class CurrentGameResult(
    val gameId: String? = null,
    val game: Game? = null,
    val error: Int? = null
)