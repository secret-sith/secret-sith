package com.secret.palpatine.data.model.invitation

/**
 * Returns a list of invites if call was successful
 * @property success Contains invites if successful
 * @property error Number of errors while querying the list of invites.
 */
data class InviteListResult(
    val success: List<Invite>? = null,
    val error: Int? = null
)