package com.secret.palpatine.data.model.friends.friendgroup

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.secret.palpatine.data.model.friends.friend.FriendsListAdapter

class FriendGroupAdapter(
    private val list: List<FriendGroup>,
    private val context: Context,
    private val listener: FriendsListAdapter.FriendListAdapterListener
) : RecyclerView.Adapter<FriendGroupViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendGroupViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return FriendGroupViewHolder(inflater, parent, listener)
    }

    override fun onBindViewHolder(holder: FriendGroupViewHolder, position: Int) {
        val friendGroup: FriendGroup = list[position]
        val gridLayoutManager = GridLayoutManager(context, 2, LinearLayoutManager.VERTICAL, false)
        holder.bind(friendGroup, context, gridLayoutManager)

    }


    override fun getItemCount(): Int = list.size
}