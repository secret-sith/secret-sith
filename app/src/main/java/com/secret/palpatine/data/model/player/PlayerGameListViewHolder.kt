package com.secret.palpatine.data.model.player

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
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


    init {
        nameTextView = itemView.findViewById(R.id.playerUserName)
        voteWrapper = itemView.findViewById(R.id.voteWrapper)
        currentVote = itemView.findViewById(R.id.currentPlayerVoteBtn)
    }

    fun bind(player: Player, game: Game?) {
        if (currentUserId == player.user) {
            nameTextView?.text = "You"

        } else {
            nameTextView?.text = context.getString(R.string.player_name, player.userName)

        }
        Log.d(
            "GamePhase", game?.phase
                .toString()
        )
        when (game?.phase) {

            GamePhase.vote -> {

                if (player.user != currentUserId) {
                    voteWrapper?.visibility = View.GONE
                }
            }
            else -> {
                voteWrapper?.visibility = View.VISIBLE

            }
        }

        when (player.vote) {

            true -> {

                currentVote?.setImageDrawable(context.getDrawable(R.drawable.ic_check_black_24dp))
                currentVote?.setColorFilter(context.getColor(R.color.successColor))
            }

            false -> {
                currentVote?.setImageDrawable(context.getDrawable(R.drawable.ic_close_black_24dp))
                currentVote?.setColorFilter(context.getColor(R.color.errorColor))
            }

            null -> {
                currentVote?.setImageDrawable(context.getDrawable(R.drawable.ic_help_black_24dp))
                currentVote?.setColorFilter(context.getColor(R.color.primaryTextColor))
            }
        }


    }
}