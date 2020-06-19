package com.secret.palpatine.data.model.friends.friend.request

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class FriendRequestsListAdapter(private val list: List<FriendRequest>, private val context: Context, private val listener: FriendRequestAcceptListener) :
    RecyclerView.Adapter<FriendRequestViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendRequestViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return FriendRequestViewHolder(inflater, parent, context, listener)
    }

    override fun onBindViewHolder(holder: FriendRequestViewHolder, position: Int) {
        val user: FriendRequest = list[position]
        holder.bind(user)
    }


    override fun getItemCount(): Int = list.size

    interface FriendRequestAcceptListener {
        fun onAccept(data: FriendRequest)
    }
}