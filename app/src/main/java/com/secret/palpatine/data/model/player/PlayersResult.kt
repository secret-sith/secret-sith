package com.secret.palpatine.data.model.player

data class PlayersResult(
    val players: List<Player>? = null,
    val error: Int? = null
)