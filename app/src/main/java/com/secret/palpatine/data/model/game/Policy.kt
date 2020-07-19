package com.secret.palpatine.data.model.game

import com.google.firebase.firestore.DocumentId

/**
 * Data class describing an instance of a playable Policy.
 *
 * @property id Identifies a policy in the database
 * @property type Identicates the type of the policy: loyalist or imperialist.
 */
data class Policy(
    @DocumentId val id: String,
    val type: PolicyType
) {
    constructor() : this("", PolicyType.loyalist)
}