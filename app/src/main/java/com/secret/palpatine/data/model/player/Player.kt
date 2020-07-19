package com.secret.palpatine.data.model.player

import com.google.firebase.firestore.DocumentId
import java.io.Serializable

/**
 * Describes the entity of a player.
 *
 * @property id Unique identifier of the player
 * @property role Assigned role, e.g. Loyalist, Imperialist or Sith
 * @property user Corresponding user reference
 * @property userName Assigned username
 * @property state Indicates whether invite has been accepted
 * @property vote Indicates last voting decision
 * @property killed Indicates whether the player is killed
 * @property isHost Indicates whether the player is the host
 * @property order Indicates where the player is positioned in the current game
 * @property selected Indicates whether the play is selected in a current decision
 */
data class Player(
    @DocumentId val id: String,
    var role: PlayerRole,
    val user: String,
    val userName: String?,
    val state: PlayerState,
    var vote: Boolean? = null,
    var killed: Boolean? = null,
    var isHost: Boolean = false,
    var order: Int? = null,
    private var selected: Boolean = false
) : Serializable {
    constructor() : this(
        "",
        PlayerRole.imperialist,
        "",
        "",
        PlayerState.pending,
        null,
        null,
        false,
        null
    )

    /**
     * Mark or unmarks the player.
     */
    fun selectPlayer() {
        selected = !selected
    }

    /**
     * Sets selection to false.
     */
    fun resetSelection() {
        selected = false
    }

    /**
     * Sets selection to true.
     */
    fun isSelected(): Boolean {
        return selected
    }


}



