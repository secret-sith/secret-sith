package com.secret.palpatine.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.secret.palpatine.R
import com.secret.palpatine.data.model.game.Game
import com.secret.palpatine.data.model.player.Player
import com.secret.palpatine.data.model.player.SelectPlayerListAdapter
import com.secret.palpatine.data.model.player.SelectedPlayerMode
import com.secret.palpatine.databinding.GameNominateChancellorFragmentBinding

class NominateChancellorFragment : Fragment(), SelectPlayerListAdapter.OnPlayerSelectedListener {
    private lateinit var binding: GameNominateChancellorFragmentBinding
    private lateinit var viewModel: GameViewModel
    private lateinit var playerListAdapter: SelectPlayerListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = GameNominateChancellorFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(GameViewModel::class.java)
        viewModel.game.observe(viewLifecycleOwner, Observer { game ->
            val players = viewModel.players.value ?: emptyList()
            updatePlayers(players, game)
        })
        viewModel.players.observe(viewLifecycleOwner, Observer { players ->
            val game = viewModel.game.value
            if (game != null) updatePlayers(players, game)
        })

        playerListAdapter =
            SelectPlayerListAdapter(
                listOf(),
                requireContext(),
                this@NominateChancellorFragment,
                SelectedPlayerMode.CHANCELLOR
            )

        binding.nominateChancellorConfirm.setOnClickListener {
            //val pos = binding.players.adapter.
            //    val player = binding.players.getItemAtPosition(pos) as Player
            if (playerListAdapter.getSelectedPlayer() != null) {
                viewModel.setChancellorCandidate(playerListAdapter.getSelectedPlayer()!!)
                requireActivity().finish()
            }
        }
    }

    private fun updatePlayers(players: List<Player>, game: Game) {
        val eligiblePlayers = getEligiblePlayers(game, players)
        playerListAdapter.list = eligiblePlayers
        binding.players.apply {
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
            adapter = playerListAdapter

        }
    }

    private fun getEligiblePlayers(game: Game, players: List<Player>): List<Player> {
        return players.filter { player ->
            if (player.id == game.presidentialCandidate?.id) return@filter false
            if (player.id == game.chancellor?.id) return@filter false
            if (player.id == game.president?.id && players.size > 5) return@filter false
            if (player.killed != null && player.killed!!) return@filter false

            return@filter true
        }
    }

    override fun onSelectPlayer(player: Player) {
        binding.nominateChancellorConfirm.isEnabled = true

        for (p in playerListAdapter.list) {
            p.resetSelection()
        }
        player.selectPlayer()

        playerListAdapter.notifyDataSetChanged()
    }
}
