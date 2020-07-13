package com.secret.palpatine.data.model.friends.friend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.secret.palpatine.R
import com.secret.palpatine.data.model.user.User
import com.secret.palpatine.ui.mainmenu.MainMenuActivity

class FriendViewHolder(inflater: LayoutInflater, parent: ViewGroup, var listner: FriendsListAdapter.FriendListAdapterListener):
    RecyclerView.ViewHolder(inflater.inflate(R.layout.object_friend,parent,false))  {

    private var nameTextView: TextView? = null
    private var layout: MaterialCardView? = null

    init {
        nameTextView = itemView.findViewById(R.id.friend_object_name_textview)
        layout = itemView.findViewById(R.id.friendListItem)

    }

    fun bind(user: User) {
        nameTextView?.text = user.username
        manageSelection(user)

        layout?.isCheckable = MainMenuActivity.isInSelectionMode

        layout?.setOnClickListener {
            user.isSelected = !user.isSelected
            manageSelection(user)
        }
    }

    private fun manageSelection(user: User){
        layout?.isChecked = user.isSelected

        listner.onSelect(user)
    }
}