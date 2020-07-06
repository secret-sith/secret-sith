package com.secret.palpatine.data.model.player

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.secret.palpatine.R
import com.secret.palpatine.data.model.PlayerRole

/**
 * Created by Florian Fuchs on 19.06.2020.
 */
class PlayerListViewHolder(
    inflater: LayoutInflater,
    parent: ViewGroup,
    var context: Context,
    private var currentUserId: String,
    private var showMembership: Boolean = false

) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.object_row_player, parent, false)) {
    private var nameTextView: TextView? = null
    private var playerStateIcon: ImageView? = null
    private var membershipLayout: LinearLayout? = null
    private var membershipImageView: ImageView? = null

    init {
        nameTextView = itemView.findViewById(R.id.playerUserName)
        playerStateIcon = itemView.findViewById(R.id.playerStateIcon)
        membershipLayout = itemView.findViewById(R.id.membershipLayout)
        membershipImageView = itemView.findViewById(R.id.playerMemberShipCard)
    }

    fun bind(player: Player) {
        if (currentUserId == player.user) {
            nameTextView?.text = "You"

        } else {
            nameTextView?.text = context.getString(R.string.player_name, player.userName)

        }

        when(player.state){

            PlayerState.accepted -> {
                playerStateIcon?.setImageDrawable(context.getDrawable(R.drawable.baseline_check_circle_outline_white_18dp))
            }
            PlayerState.declined -> {
                playerStateIcon?.setImageDrawable(context.getDrawable(R.drawable.baseline_cancel_white_18dp))

            }
            PlayerState.pending -> {
                playerStateIcon?.setImageDrawable(context.getDrawable(R.drawable.baseline_pending_white_18dp))

            }
        }

        if(showMembership){
            membershipLayout?.visibility = View.VISIBLE
            when(player.role){

                PlayerRole.imperialist ->{
                    membershipImageView?.setImageDrawable(context.getDrawable(R.drawable.membership_imperialist))
                }
                PlayerRole.loyalist ->{
                    membershipImageView?.setImageDrawable(context.getDrawable(R.drawable.membership_loyalist))
                }
                PlayerRole.sith ->{
                    membershipImageView?.setImageDrawable(context.getDrawable(R.drawable.secret_role_sith))
                }
            }
        }else {
            membershipLayout?.visibility = View.GONE

        }
        stateTextView?.text = context.getString(R.string.player_role, player.role.toString().capitalize())
    }

}