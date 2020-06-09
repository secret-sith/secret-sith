package com.secret.palpatine.data.model.friends.friend.request

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.secret.palpatine.data.model.friends.friend.FriendViewHolder
import com.secret.palpatine.data.model.User

class FriendRequestsListAdapter(private val list: List<User>, private val context: Context) :
    RecyclerView.Adapter<FriendRequestViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendRequestViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return FriendRequestViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: FriendRequestViewHolder, position: Int) {
        val user: User = list[position]
        holder.bind(user, context)
    }


    override fun getItemCount(): Int = list.size
}