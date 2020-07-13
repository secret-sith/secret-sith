package com.secret.palpatine.data.model.user

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference
import com.secret.palpatine.data.model.game.Game
import java.io.Serializable
import java.util.*

data class User(
    @DocumentId val id: String,
    val username: String,
    val createdAt: String,
    val deviceID: String?,
    val loginID: UUID?,
    val currentGame: String?,
    var isSelected: Boolean = false,
    var friends: List<DocumentReference>
) : Serializable {
    constructor() : this("", "", "", "", null, null, false, listOf())

}