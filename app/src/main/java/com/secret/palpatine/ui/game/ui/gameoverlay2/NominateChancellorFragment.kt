package com.secret.palpatine.ui.game.ui.gameoverlay2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.secret.palpatine.data.model.game.Game
import com.secret.palpatine.data.model.player.Player
import com.secret.palpatine.data.model.player.SelectPlayerListAdapter
import com.secret.palpatine.databinding.GameNominateChancellorFragmentBinding

class NominateChancellorFragment : Fragment() {
    companion object {
        fun newInstance(): NominateChancellorFragment = NominateChancellorFragment()
    }

    private lateinit var binding: GameNominateChancellorFragmentBinding
    private lateinit var viewModel: GameOverlay2ViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = GameNominateChancellorFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(GameOverlay2ViewModel::class.java)
        viewModel.game.observe(viewLifecycleOwner, Observer { game ->
            val players = viewModel.players.value ?: emptyList()
            updatePlayers(players, game)
        })
        viewModel.players.observe(viewLifecycleOwner, Observer { players ->
            val game = viewModel.game.value
            updatePlayers(players, game)
        })
        binding.players.onItemClickListener = AdapterView.OnItemClickListener { _, _, _, _ ->
            binding.confirm.isEnabled = binding.players.checkedItemCount > 0
        }
        binding.confirm.setOnClickListener {
            val pos = binding.players.checkedItemPosition
            val player = binding.players.getItemAtPosition(pos) as Player
            viewModel.setChancellorCandidate(player)
            requireActivity().finish()
        }
    }

    private fun updatePlayers(players: List<Player>, game: Game?) {
        val eligiblePlayers = players.filter { it.id != game?.presidentialCandidate?.id }
        binding.players.apply {
            adapter = SelectPlayerListAdapter(eligiblePlayers, context)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
