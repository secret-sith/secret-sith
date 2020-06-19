package com.secret.palpatine.data.model.player

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.secret.palpatine.R
import com.secret.palpatine.data.model.PlayerRole

/**
 * Created by Florian Fuchs on 19.06.2020.
 */
class PlayerListViewHolder(inflater: LayoutInflater, parent: ViewGroup, var context: Context) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.object_row_player, parent, false)) {
    private var nameTextView: TextView? = null
    private var stateTextView: TextView? = null

    init {
        nameTextView = itemView.findViewById(R.id.playerUserName)
        stateTextView = itemView.findViewById(R.id.playerState)
    }

    fun bind(player: Player) {
        nameTextView?.text = context.getString(R.string.player_name, player.userName)
        stateTextView?.text = context.getString(R.string.player_state, player.state.toString())
    }

}