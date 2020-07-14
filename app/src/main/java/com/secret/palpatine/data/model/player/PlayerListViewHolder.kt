package com.secret.palpatine.data.model.player

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
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
    private var membershipLayout: ConstraintLayout? = null
    private var membershipImageView: ImageView? = null
    private var stateTextView: TextView? = null

    init {
        nameTextView = itemView.findViewById(R.id.playerUserName)
        membershipLayout = itemView.findViewById(R.id.membershipLayout)
        playerStateIcon = itemView.findViewById(R.id.playerStateIcon)
        stateTextView = itemView.findViewById(R.id.playerRole)
        membershipImageView = itemView.findViewById(R.id.membershipImageView)

    }

    fun bind(player: Player) {
        if (currentUserId == player.user) {
            nameTextView?.text = "You"

        } else {
            nameTextView?.text = context.getString(R.string.player_name, player.userName)

        }

        when (player.state) {

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

        if (showMembership) {
            membershipLayout?.visibility = View.VISIBLE
            when (player.role) {
                PlayerRole.imperialist -> {
                    membershipImageView?.setImageResource(R.drawable.role_evil_2)
                    stateTextView?.setTextColor(context.getColor(R.color.evilColor))

                }
                PlayerRole.loyalist -> {
                    membershipImageView?.setImageResource(R.drawable.role_good_3)
                    stateTextView?.setTextColor(context.getColor(R.color.goodColor))

                }
                PlayerRole.sith -> {
                    membershipImageView?.setImageResource(R.drawable.role_evil_leader)
                    stateTextView?.setTextColor(context.getColor(R.color.evilColor))
                }
            }

        } else {
            membershipLayout?.visibility = View.GONE

        }
        stateTextView?.text =
            context.getString(R.string.player_role, player.role.toString().capitalize())
    }

}