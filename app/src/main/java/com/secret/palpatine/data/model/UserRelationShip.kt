package com.secret.palpatine.data.model

import com.secret.palpatine.data.model.user.User

data class UserRelationShip(
    val user_first: User,
    val user_second: User,
    val type: RelationshipType
)