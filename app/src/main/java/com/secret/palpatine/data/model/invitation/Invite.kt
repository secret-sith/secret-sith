package com.secret.palpatine.data.model.invitation

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import java.io.Serializable

/**
 * Model for an invite
 *
 * @property id Unique identifier of the invite
 * @property name Name of the invited user
 * @property timestamp Time when invite was sent
 * @property gameId ID of the Game where the invite is valid
 * @property from sender of the invite
 * @property invitationText Text to display.
 */
data class Invite(
    @DocumentId val id: String,
    val name: String,
    val timestamp: Timestamp,
    val gameId: String,
    val from: String,
    val invitationText: String?
) : Serializable {
    constructor() : this("", "", Timestamp.now(), "", "", "")
}