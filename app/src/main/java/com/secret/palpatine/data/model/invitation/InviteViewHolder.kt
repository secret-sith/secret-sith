package com.secret.palpatine.data.model.invitation

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.secret.palpatine.R
import com.secret.palpatine.util.getPassedTimeFormatted

class InviteViewHolder(
    inflater: LayoutInflater,
    parent: ViewGroup,
    private val acceptInviteListener: InviteListAdapter.AcceptInviteListener
) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.object_invite, parent, false)) {
    private var nameTextView: TextView? = null
    private var dateTextView: TextView? = null
    private var invitationtextTextView: TextView? = null
    private var joinBtn: Button? = null

    init {
        nameTextView = itemView.findViewById(R.id.name_textview)
        dateTextView = itemView.findViewById(R.id.date_textview)
        invitationtextTextView = itemView.findViewById(R.id.invitationtext_textview)
        joinBtn = itemView.findViewById(R.id.join_button)
    }

    fun bind(invite: Invite) {
        nameTextView?.text = invite.from

        val text = getPassedTimeFormatted(invite.timestamp)
        dateTextView?.text = text

        if (invite.invitationText != null) {
            invitationtextTextView?.text = invite.invitationText

        } else {
            invitationtextTextView?.text = "No description provided"
        }

        joinBtn?.setOnClickListener {
            acceptInviteListener.onAccept(invite)
        }
    }
}