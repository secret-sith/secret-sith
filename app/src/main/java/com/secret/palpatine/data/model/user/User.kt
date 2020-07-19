package com.secret.palpatine.data.model.user

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference
import java.io.Serializable
import java.util.*

/**
 * Data model for the user.
 *
 * @property id Unique identifier
 * @property username Nickname of the user
 * @property createdAt Timestamp of creation
 * @property deviceID ID of user device
 * @property loginID Login ID
 * @property currentGame ID of the current Game
 * @property isSelected Indicator if user is selected
 * @property friends References to friends
 */
data class User(
    @DocumentId val id: String,
    val username: String,
    val createdAt: String,
    val deviceID: String?,
    val loginID: UUID?,
    val currentGame: String?,
    var isSelected: Boolean = false,
    var friends: List<DocumentReference>
) : Serializable {
    constructor() : this("", "", "", "", null, null, false, listOf())

}