package com.secret.palpatine.data.model

import com.secret.palpatine.data.model.Game
import java.io.Serializable
import java.util.*

data class User(
    val username: String,
    val createdAt: String,
    val deviceID: String?,
    val loginID: UUID?,
    val currentGame: Game?,
    var isSelected: Boolean = false
): Serializable {

    constructor() : this("","","",null,null, false)

}