package com.secret.palpatine.data.model.player

/**
 * Describes the states which a player can have
 *
 * pending --> Player has not yet accepted the invite
 * accepted --> Player accepted invite
 * decline --> Player declined the invite
 */
enum class PlayerState {
    pending, accepted, declined
}