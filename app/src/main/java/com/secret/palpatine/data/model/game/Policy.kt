package com.secret.palpatine.data.model.game

import com.google.firebase.firestore.DocumentId

data class Policy(
    @DocumentId val id: String,
    val type: PolicyType
) {
    constructor() : this("", PolicyType.loyalist)
}