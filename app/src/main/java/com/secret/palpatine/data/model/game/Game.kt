package com.secret.palpatine.data.model.game

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference
import java.io.Serializable

/**
 * Data class holding all relevant information for the current game.
 *
 * @property failedGovernments Number of failed Governments
 * @property imperialPolitics Number of imperial Politics played so far
 * @property loyalistPolitics Number of loyalist Politics played so far
 * @property phase Current phase of the game.
 * @property state Current state of the game. Indicates if game is pending, running or finished
 * @property host ID of player, which is hosting the game
 * @property presidentialCandidate Player, which is running for president in the active round
 * @property winner Indicates which party has won the game
 * @property chancellorCandidate Player, which is running for chancellor in the active round
 * @property president Player, who is elected as president.
 * @property chancellor Player, who is elected as chancellor.
 */
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
) : Serializable {
    constructor() : this(
        "", 0, 0, 0, GamePhase.nominate_chancellor, GameState.pending,
        "", null, null, null, null, null
    )
}