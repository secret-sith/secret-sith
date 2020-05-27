package com.example.secret.palpatine.data.model.friends.friendgroup

import com.example.secret.palpatine.data.model.friends.friend.Friend

data class FriendGroup(
    val letter: Char,
    var friendList: MutableList<Friend>
)