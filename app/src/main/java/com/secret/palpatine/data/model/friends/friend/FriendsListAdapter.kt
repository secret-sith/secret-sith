package com.secret.palpatine.data.model.friends.friend

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.secret.palpatine.data.model.friends.friend.FriendViewHolder
import com.secret.palpatine.data.model.User
import java.util.logging.Filter

class FriendsListAdapter(private val list: List<User>): RecyclerView.Adapter<FriendViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return FriendViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val user: User = list[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int = list.size

}