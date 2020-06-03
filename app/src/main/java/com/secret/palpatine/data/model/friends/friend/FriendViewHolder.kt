package com.secret.palpatine.data.model.friends.friend

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.secret.palpatine.R

class FriendViewHolder(inflater: LayoutInflater, parent: ViewGroup):
    RecyclerView.ViewHolder(inflater.inflate(R.layout.object_friend,parent,false)) {

    private var nameTextView: TextView? = null
    private var selectedButton: ImageButton? = null

    init {
        nameTextView = itemView.findViewById(R.id.friend_object_name_textview)
        selectedButton = itemView.findViewById(R.id.friend_object_selectedButton)

    }

    fun bind(friend: Friend) {
        nameTextView?.text = friend.name
        manageSelection(friend)
        selectedButton?.setOnClickListener {
            friend.isSelected = !friend.isSelected
            manageSelection(friend)
        }
    }

    fun manageSelection(friend: Friend){
        if(friend.isSelected){  selectedButton?.setImageResource(R.drawable.ic_check_box_black_24dp) }
        else { selectedButton?.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp) }
    }
}