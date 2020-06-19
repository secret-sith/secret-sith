package com.secret.palpatine.data.model.friends.friend.request

import com.secret.palpatine.data.model.user.User
import java.io.Serializable

/**
 * Created by Florian Fuchs on 10.06.2020.
 */
data class FriendRequest(val id: String, var user: User?): Serializable {

    constructor() : this("", null)

}