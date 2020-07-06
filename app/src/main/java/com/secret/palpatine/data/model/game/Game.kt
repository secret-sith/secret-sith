package com.secret.palpatine.data.model.game

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference
import com.secret.palpatine.data.model.player.Player
import java.io.Serializable
import com.secret.palpatine.data.model.Election

data class Game(
    @DocumentId val id: String,
    val failedGovernments: Int,
    val imperialPolitics: Int,
    val loyalistPolitics: Int,
    val phase: GamePhase,
    val state: GameState,
    val host: String,
    val presidentialCandidate: DocumentReference?,
    val winner: String?,
    val chancellorCandidate: DocumentReference?,
    val president: DocumentReference?,
    val chancellor: DocumentReference?
) : Serializable{
     constructor() : this("", 0, 0, 0, GamePhase.nominate_chancellor, GameState.pending,
                       "", null, null, null, null, null)
}