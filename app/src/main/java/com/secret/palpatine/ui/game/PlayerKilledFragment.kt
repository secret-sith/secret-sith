package com.secret.palpatine.ui.game

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.secret.palpatine.R
import com.secret.palpatine.data.model.game.Game
import com.secret.palpatine.data.model.player.Player
import com.secret.palpatine.data.model.player.PlayerListAdapter
import com.secret.palpatine.data.model.player.SelectPlayerListAdapter
import com.secret.palpatine.data.model.player.SelectedPlayerMode
import com.secret.palpatine.databinding.GamePlayerKilledFragmentBinding
import com.secret.palpatine.databinding.GameVoteFragmentBinding
import com.secret.palpatine.util.VOTE

class PlayerKilledFragment : Fragment() {
    private lateinit var viewModel: GameViewModel
    private lateinit var binding: GamePlayerKilledFragmentBinding
    private var auth: FirebaseAuth = Firebase.auth
    private lateinit var playerListAdapter: PlayerListAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = GamePlayerKilledFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(GameViewModel::class.java)

        viewModel.game.observe(viewLifecycleOwner, Observer { game ->
            val players = viewModel.players.value ?: emptyList()
            updatePlayers(players)
        })
        viewModel.players.observe(viewLifecycleOwner, Observer { players ->
            updatePlayers(players)
        })

        playerListAdapter =
            PlayerListAdapter(
                listOf(),
                requireContext(),
                auth.currentUser!!.uid,
                showMembership = true,
                showSate = false
            )
        binding.gameEndPlayerList.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = playerListAdapter
        }

        viewModel.players.observe(viewLifecycleOwner, Observer {

        })

    }

    private fun updatePlayers(players: List<Player>) {
        playerListAdapter.list = players
        playerListAdapter.notifyDataSetChanged()

    }

}