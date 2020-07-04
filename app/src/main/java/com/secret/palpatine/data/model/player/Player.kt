package com.secret.palpatine.data.model.player

import com.google.firebase.firestore.DocumentId
import com.secret.palpatine.data.model.PlayerRole
import com.secret.palpatine.data.model.game.Game
import com.secret.palpatine.data.model.user.User
import java.io.Serializable

data class Player(
    @DocumentId val id: String,
    val role: PlayerRole?,
    val user: String,
    val userName: String?,
    val state: PlayerState,
    var vote: Boolean? = null,
    val index: Int
) : Serializable {
    constructor() : this("", PlayerRole.IMPERIALIST, "", "", PlayerState.pending, null, 0)

}



