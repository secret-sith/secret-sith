package com.secret.palpatine.data.model.player

import android.app.ListActivity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.secret.palpatine.R

class SelectPlayerListAdapter(
    private val players: List<Player>,
    context: Context
) : ArrayAdapter<Player>(context, R.layout.select_player, players) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView ?: {
            val inflater = context.getSystemService(LayoutInflater::class.java)
            inflater.inflate(R.layout.select_player, parent, false)
        }()
        val player = players[position]
        val textView = view.findViewById<TextView>(R.id.name)
        textView.text = player.userName
        return view
    }
}