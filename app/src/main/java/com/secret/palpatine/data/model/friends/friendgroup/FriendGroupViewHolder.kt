package com.secret.palpatine.data.model.friends.friendgroup

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.secret.palpatine.R
import com.secret.palpatine.data.model.friends.friend.FriendsListAdapter

class FriendGroupViewHolder(
    inflater: LayoutInflater,
    parent: ViewGroup,
    private val listener: FriendsListAdapter.FriendListAdapterListener
) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.object_friendgroup, parent, false)) {

    private var letterTextView: TextView? = null
    private var recyclerView: RecyclerView? = null

    init {
        letterTextView = itemView.findViewById(R.id.friendsmenu_group_letter)
        recyclerView = itemView.findViewById(R.id.friendsmenu_group_recyclerview)
    }

    fun bind(friendGroup: FriendGroup, context: Context, gridLayoutManager: GridLayoutManager) {
        letterTextView?.text = friendGroup.letter.toString()
        recyclerView?.apply {
            layoutManager = gridLayoutManager
            adapter = FriendsListAdapter(friendGroup.friendList, listener)
        }
    }
}