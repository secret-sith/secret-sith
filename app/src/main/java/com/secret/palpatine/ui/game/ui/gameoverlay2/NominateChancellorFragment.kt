package com.secret.palpatine.ui.game.ui.gameoverlay2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.secret.palpatine.data.model.player.PlayerListAdapter
import com.secret.palpatine.databinding.GameNominateChancellorFragmentBinding

class NominateChancellorFragment : Fragment() {
    companion object {
        fun newInstance() = NominateChancellorFragment()
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
        viewModel.players.observe(viewLifecycleOwner, Observer { players ->
            binding.debug.text = players.toString()
            binding.selectChancellorList.apply {
                adapter = PlayerListAdapter(players, context, "")
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
