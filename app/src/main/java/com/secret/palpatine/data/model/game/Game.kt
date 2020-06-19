package com.secret.palpatine.data.model.game

import com.google.firebase.firestore.DocumentId
import java.io.Serializable

data class Game(
    @DocumentId val id: String,
    val failedGoverments: Int,
    val imperialPolitics: Int,
    val loylistPolitics: Int,
    val phase: GamePhase,
    val state: GameState,
    val host: String
) : Serializable {
    constructor() : this("", 0, 0, 0, GamePhase.vote, GameState.pending, "")

}