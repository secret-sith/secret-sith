package com.secret.palpatine.data.model.player

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.secret.palpatine.data.model.PlayerRole
import com.secret.palpatine.data.model.game.Game

/**
 * Created by Florian Fuchs on 10.07.2020.
 */
class PlayerGameListAdapter(
    private var list: List<Player>,
    private var context: Context,
    private var currentUserId: String
) :
    RecyclerView.Adapter<PlayerGameListViewHolder>() {
    var game: Game? = null
    var isEvil: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerGameListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PlayerGameListViewHolder(inflater, parent, context, currentUserId)
    }

    override fun onBindViewHolder(holder: PlayerGameListViewHolder, position: Int) {
        val player: Player = list[position]
        holder.bind(player, game, isEvil)
    }

    fun setItems(players: List<Player>) {
        list = players.sortedBy { player -> player.order }
        var currentPlayer = players.findLast { player -> player.user.equals(currentUserId) }
        isEvil = currentPlayer != null && currentPlayer?.role != PlayerRole.loyalist
    }

    override fun getItemCount(): Int = list.size
}