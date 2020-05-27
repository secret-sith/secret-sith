package com.example.secret.palpatine.data.model.invitation

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.secret.palpatine.R
import com.example.secret.palpatine.util.getPassedTimeFormatted

class InviteViewHolder(inflater: LayoutInflater, parent: ViewGroup):
    RecyclerView.ViewHolder(inflater.inflate(R.layout.object_invite,parent,false)) {
    private var nameTextView: TextView? = null
    private var dateTextView: TextView? = null
    private var invitationtextTextView: TextView? = null

    init {
        nameTextView = itemView.findViewById(R.id.name_textview)
        dateTextView = itemView.findViewById(R.id.date_textview)
        invitationtextTextView = itemView.findViewById(R.id.invitationtext_textview)
    }

    fun bind(invite: Invite){
        nameTextView?.text = invite.name

        //dateTextView?.text = invite.date.toString()
        val text = getPassedTimeFormatted(invite.date)
        dateTextView?.text = text

        invitationtextTextView?.text = invite.invitationText
    }
}