package com.secret.palpatine.data.model.invitation

import java.time.LocalDateTime

data class Invite(
    val name: String,
    val date: LocalDateTime,
    val invitationText: String
)