package com.secret.palpatine.data.model.friends.friend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.secret.palpatine.R
import com.secret.palpatine.data.model.User
import com.secret.palpatine.ui.mainmenu.MainMenuActivity

class FriendViewHolder(inflater: LayoutInflater, parent: ViewGroup):
    RecyclerView.ViewHolder(inflater.inflate(R.layout.object_friend,parent,false)) {

    private var nameTextView: TextView? = null
    private var selectedButton: ImageButton? = null

    init {
        nameTextView = itemView.findViewById(R.id.friend_object_name_textview)
        selectedButton = itemView.findViewById(R.id.friend_object_selectedButton)

    }

    fun bind(user: User) {
        nameTextView?.text = user.userName
        manageSelection(user)

        selectedButton?.visibility = if(MainMenuActivity.isInSelectionMode) View.VISIBLE else View.INVISIBLE

        selectedButton?.setOnClickListener {
            user.isSelected = !user.isSelected
            manageSelection(user)
        }
    }

    private fun manageSelection(user: User){
        if(user.isSelected){  selectedButton?.setImageResource(R.drawable.ic_check_box_black_24dp) }
        else { selectedButton?.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp) }
    }
}