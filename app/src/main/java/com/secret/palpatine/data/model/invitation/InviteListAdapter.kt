package com.secret.palpatine.data.model.invitation


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class InviteListAdapter(
    private val list: List<Invite>,
    private val acceptInviteListener: InviteListAdapter.AcceptInviteListener
) : RecyclerView.Adapter<InviteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InviteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return InviteViewHolder(inflater, parent, acceptInviteListener)
    }

    override fun onBindViewHolder(holder: InviteViewHolder, position: Int) {
        val invite: Invite = list[position]
        holder.bind(invite)
    }


    override fun getItemCount(): Int = list.size


    interface AcceptInviteListener {
        fun onAccept(data: Invite)
    }

}