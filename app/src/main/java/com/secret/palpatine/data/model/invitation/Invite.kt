package com.secret.palpatine.data.model.invitation

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import java.io.Serializable

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