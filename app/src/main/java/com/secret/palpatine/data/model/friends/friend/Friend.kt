package com.secret.palpatine.data.model.friends.friend

import java.io.Serializable

data class Friend(
    val name: String,
    var isSelected: Boolean = false
) : Serializable {

    constructor() : this("", false)

}