package com.secret.palpatine.data.model.game

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference

data class Game(
    @DocumentId val id: String,
    val failedGoverments: Int,
    val imperialPolitics: Int,
    val loylistPolitics: Int,
    val phase: GamePhase,
    val state: GameState,
    val host: String,
    val president: DocumentReference?
) {
    constructor() : this("", 0, 0, 0, GamePhase.nominate_chancellor, GameState.pending, "", null)
}