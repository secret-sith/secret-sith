package com.secret.palpatine.data.model.invitation

import com.secret.palpatine.data.model.user.User


/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
data class InviteListResult(
    val success: List<Invite>? = null,
    val error: Int? = null
)