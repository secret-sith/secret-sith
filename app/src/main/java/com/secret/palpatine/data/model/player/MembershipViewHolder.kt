package com.secret.palpatine.data.model.player

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.secret.palpatine.R
import com.secret.palpatine.data.model.Player
import com.secret.palpatine.data.model.PlayerRole

class MembershipViewHolder(inflater: LayoutInflater, parent: ViewGroup):
    RecyclerView.ViewHolder(inflater.inflate(R.layout.object_membership,parent,false)) {
    private var nameTextView: TextView? = null
    private var avatarImageView: ImageView? = null
    private var membershipImageView: ImageView? = null

    init {
        nameTextView = itemView.findViewById(R.id.member_name)
        avatarImageView = itemView.findViewById(R.id.player_avatar)
        membershipImageView = itemView.findViewById(R.id.membership_image)
    }

    fun bind(player: Player){
        nameTextView?.text = player.user.username
        when(player.role){
            PlayerRole.LOYALIST -> membershipImageView?.setImageResource(R.drawable.secret_role_loyalist)
            PlayerRole.IMPERIALIST -> membershipImageView?.setImageResource(R.drawable.secret_role_imperialist)
            PlayerRole.SECRET_PALPATINE -> membershipImageView?.setImageResource(R.drawable.ic_secret_role_sith)
        }
    }
}