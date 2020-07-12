package com.secret.palpatine.data.model.player

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.secret.palpatine.R
import com.secret.palpatine.data.model.game.Game
import com.secret.palpatine.data.model.game.GamePhase

/**
 * Created by Florian Fuchs on 19.06.2020.
 */
class SelectPlayerListViewHolder(
    inflater: LayoutInflater,
    parent: ViewGroup,
    var context: Context,
    private val listener: SelectPlayerListAdapter.OnPlayerSelectedListener
) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.select_player, parent, false)) {
    private var layout: MaterialCardView? = null
    private var nameTextView: TextView? = null


    init {
        nameTextView = itemView.findViewById(R.id.name)
        layout = itemView.findViewById(R.id.selectPlayerLayout)
    }

    fun bind(player: Player, mode: SelectedPlayerMode) {
        nameTextView?.text = player.userName

        layout?.setOnClickListener {
            listener.onSelectPlayer(player)
        }

        when (mode) {
            SelectedPlayerMode.KILL -> {
                layout?.setCheckedIconResource(R.drawable.ic_cancel_black_24dp)
                layout?.checkedIcon?.setTint(context.getColor(R.color.errorColor))
            }
        }
        layout?.isChecked = player.isSelected()

    }
}