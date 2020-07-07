package com.secret.palpatine.data.model.player

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import com.secret.palpatine.data.model.PlayerRole
import java.io.Serializable

data class Player(
    @DocumentId val id: String,
    var role: PlayerRole?,
    val user: String,
    val userName: String?,
    val state: PlayerState,
    var vote: Boolean? = null
) : Serializable {
    constructor() : this("", PlayerRole.imperialist, "", "", PlayerState.pending, false)

}



