package com.secret.palpatine.data.model

import java.io.Serializable

data class Election(
    var counter: Int = 0,
    var votes_left: Int
): Serializable {
    constructor() : this(0, 100)

}