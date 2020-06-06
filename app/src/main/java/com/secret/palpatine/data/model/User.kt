package com.secret.palpatine.data.model

import com.secret.palpatine.data.model.Game
import java.util.*

data class User(
    val userName: String,
    val createdAt: String,
    val deviceID: String,
    val loginID: UUID,
    val currentGame: Game,
    var isSelected: Boolean = false
)