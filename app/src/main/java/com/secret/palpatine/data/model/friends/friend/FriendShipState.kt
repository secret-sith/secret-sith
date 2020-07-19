package com.secret.palpatine.data.model.friends.friend

/**
 * Represents the possible states of friendships
 *
 * requested --> Awaits result of friend request
 * friends --> Users are friends
 */
enum class FriendShipState {

    requested, friends
}