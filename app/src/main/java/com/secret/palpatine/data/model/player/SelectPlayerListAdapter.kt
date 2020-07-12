package com.secret.palpatine.data.model.player

import android.app.ListActivity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.secret.palpatine.R
import com.secret.palpatine.data.model.game.Game

class SelectPlayerListAdapter(
    var list: List<Player>,
    private var context: Context,
    private val listener: OnPlayerSelectedListener,
    private val mode: SelectedPlayerMode = SelectedPlayerMode.CHANCELLOR
) :
    RecyclerView.Adapter<SelectPlayerListViewHolder>() {
    var game: Game? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectPlayerListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return SelectPlayerListViewHolder(inflater, parent, context, listener)
    }

    override fun onBindViewHolder(holder: SelectPlayerListViewHolder, position: Int) {
        val player: Player = list[position]
        holder.bind(player, mode)
    }

    fun getSelectedPlayer(): Player? {

        return list.findLast { player -> player.isSelected() }
    }

    override fun getItemCount(): Int = list.size


    interface OnPlayerSelectedListener {
        fun onSelectPlayer(player: Player)
    }
}