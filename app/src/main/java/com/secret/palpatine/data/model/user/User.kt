package com.secret.palpatine.data.model.user

import com.google.firebase.firestore.DocumentId
import com.secret.palpatine.data.model.game.Game
import java.io.Serializable
import java.util.*

data class User(
    @DocumentId val id: String,
    val username: String,
    val createdAt: String,
    val deviceID: String?,
    val loginID: UUID?,
    val currentGame: Game?,
    var isSelected: Boolean = false
): Serializable {
    constructor() : this("","","","",null,null, false)
}