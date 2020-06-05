package com.example.secret.palpatine.data.model.player

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.secret.palpatine.data.model.Player

class MembershipListAdapter(private val list: List<Player>): RecyclerView.Adapter<MembershipViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MembershipViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MembershipViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: MembershipViewHolder, position: Int) {
        val player: Player = list[position]
        holder.bind(player)
    }


    override fun getItemCount(): Int = list.size
}