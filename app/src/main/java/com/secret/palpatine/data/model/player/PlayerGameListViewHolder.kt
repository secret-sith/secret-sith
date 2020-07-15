package com.secret.palpatine.data.model.player

import android.content.Context
import android.util.Log
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

    init {
        nameTextView = itemView.findViewById(R.id.playerUserName)
        voteWrapper = itemView.findViewById(R.id.voteWrapper)
        currentVote = itemView.findViewById(R.id.currentPlayerVoteBtn)
        playerIcon = itemView.findViewById(R.id.playerIcon)
    }

    fun bind(player: Player, game: Game?) {
        if (currentUserId == player.user) {
            nameTextView?.text = "You"

        } else {
            nameTextView?.text = context.getString(R.string.player_name, player.userName)

        }



        when (game?.phase) {

            GamePhase.vote -> {

                if (player.user != currentUserId) {
                    playerIcon?.imageTintList =
                        ContextCompat.getColorStateList(context, R.color.material_on_surface_emphasis_high_type)
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
                    ContextCompat.getColorStateList(context, R.color.material_on_surface_emphasis_high_type)

            }
        }


    }
}