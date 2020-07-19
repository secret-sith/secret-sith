package com.secret.palpatine.data.model.player

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.secret.palpatine.R
import com.secret.palpatine.data.model.game.Game
import com.secret.palpatine.data.model.game.GamePhase

/**
 * Created by Florian Fuchs on 19.06.2020.
 */
class PlayerGameListViewHolder(
    inflater: LayoutInflater,
    parent: ViewGroup,
    var context: Context,
    private var currentUserId: String

) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.object_game_player, parent, false)) {
    private var nameTextView: TextView? = null
    private var voteWrapper: LinearLayout? = null
    private var currentVote: ImageButton? = null
    private var playerIcon: ImageView? = null
    private var playerRoleImage: ImageView? = null
    private var txtPlayerOrder: TextView? = null

    init {
        nameTextView = itemView.findViewById(R.id.playerUserName)
        voteWrapper = itemView.findViewById(R.id.voteWrapper)
        currentVote = itemView.findViewById(R.id.currentPlayerVoteBtn)
        playerIcon = itemView.findViewById(R.id.playerIcon)
        playerRoleImage = itemView.findViewById(R.id.player_role)
        txtPlayerOrder = itemView.findViewById(R.id.txtPlayerOrder)
    }

    fun bind(player: Player, game: Game?, isEvil: Boolean) {
        txtPlayerOrder?.text = "${player.order}."

        if (currentUserId == player.user) {
            nameTextView?.text = "You"

        } else {
            nameTextView?.text = context.getString(R.string.player_name, player.userName)

        }

        if (isEvil) {
            playerRoleImage?.visibility = View.VISIBLE
            when (player.role) {
                PlayerRole.loyalist -> playerRoleImage?.visibility = View.GONE
                PlayerRole.imperialist -> playerRoleImage?.setImageDrawable(context.getDrawable(R.drawable.role_evil_2))
                PlayerRole.sith -> playerRoleImage?.setImageDrawable(context.getDrawable(R.drawable.role_evil_leader))
            }
        }




        when (game?.phase) {

            GamePhase.vote -> {

                if (player.user != currentUserId) {
                    playerIcon?.imageTintList =
                        ContextCompat.getColorStateList(
                            context,
                            R.color.material_on_surface_emphasis_high_type
                        )
                    return
                } else {
                    if (player.killed != null && player.killed!!) {
                        return
                    }
                }
            }
        }

        when (player.vote) {

            true -> {

                playerIcon?.imageTintList =
                    ContextCompat.getColorStateList(context, R.color.successColor)
            }

            false -> {
                playerIcon?.imageTintList =
                    ContextCompat.getColorStateList(context, R.color.errorColor)

            }

            null -> {
                playerIcon?.imageTintList =
                    ContextCompat.getColorStateList(
                        context,
                        R.color.material_on_surface_emphasis_high_type
                    )

            }
        }


    }
}