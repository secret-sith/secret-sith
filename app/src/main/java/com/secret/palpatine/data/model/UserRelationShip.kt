package com.secret.palpatine.data.model

data class UserRelationShip(
    val user_first: User,
    val user_second: User,
    val type: RelationshipType
)