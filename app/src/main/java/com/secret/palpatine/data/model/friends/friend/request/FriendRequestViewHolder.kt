package com.secret.palpatine.data.model.friends.friend.request

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.secret.palpatine.R
import com.secret.palpatine.data.model.User
import com.secret.palpatine.ui.mainmenu.MainMenuActivity

class FriendRequestViewHolder(inflater: LayoutInflater, parent: ViewGroup):
    RecyclerView.ViewHolder(inflater.inflate(R.layout.object_friendrequest,parent,false)) {

    private var nameTextView: TextView? = null
    private var acceptButton: Button? = null

    init {
        nameTextView = itemView.findViewById(R.id.friend_object_name_textview)
        acceptButton = itemView.findViewById(R.id.acceptAsFriend)

    }

    fun bind(user: User, context:Context) {
        nameTextView?.text = context.getString(R.string.friendRequest, user.username)
        acceptButton?.setOnClickListener {
            manageUserAccepts(user)
        }
    }

    private fun manageUserAccepts(user: User){

    }
}