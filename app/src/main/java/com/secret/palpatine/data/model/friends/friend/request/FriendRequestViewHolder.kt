package com.secret.palpatine.data.model.friends.friend.request

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.secret.palpatine.R

class FriendRequestViewHolder(
    inflater: LayoutInflater,
    parent: ViewGroup,
    var context: Context,
    var acceptListener: FriendRequestsListAdapter.FriendRequestAcceptListener
) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.object_friendrequest, parent, false)) {

    private var nameTextView: TextView? = null
    private var acceptButton: Button? = null


    init {
        nameTextView = itemView.findViewById(R.id.friend_object_name_textview)
        acceptButton = itemView.findViewById(R.id.acceptAsFriend)

    }

    fun bind(request: FriendRequest) {
        nameTextView?.text = context.getString(R.string.friendRequest, request.user?.username)
        acceptButton?.setOnClickListener {
            manageUserAccepts(request)
        }
    }

    private fun manageUserAccepts(request: FriendRequest) {

        acceptListener.onAccept(request)

    }

}

