package com.secret.palpatine.data.model.friends.friendgroup

import com.secret.palpatine.data.model.friends.friend.Friend

data class FriendGroup(
    val letter: Char,
    var friendList: MutableList<Friend>
)