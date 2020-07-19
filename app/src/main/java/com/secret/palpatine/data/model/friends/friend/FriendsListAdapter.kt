package com.secret.palpatine.data.model.friends.friend

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.secret.palpatine.data.model.user.User

class FriendsListAdapter(
    private var list: List<User>,
    private val listener: FriendListAdapterListener
) : RecyclerView.Adapter<FriendViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return FriendViewHolder(inflater, parent, listener)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val user: User = list[position]
        holder.bind(user)
    }

    fun setItems(users: List<User>) {
        list = users
    }

    override fun getItemCount(): Int = list.size


    interface FriendListAdapterListener {
        fun onSelect(data: User)
    }
}