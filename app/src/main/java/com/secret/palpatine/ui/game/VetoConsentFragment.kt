package com.secret.palpatine.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.secret.palpatine.R
import com.secret.palpatine.data.model.game.GamePhase
import com.secret.palpatine.databinding.GameVetoConsentFragmentBinding

class VetoConsentFragment : Fragment() {
    private lateinit var viewModel: GameViewModel
    private lateinit var binding: GameVetoConsentFragmentBinding

    private var chancellor: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = GameVetoConsentFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(GameViewModel::class.java)

        binding.buttonVoteYesVeto.setOnClickListener {
            submitVote(true)
        }

        binding.buttonVoteNoVeto.setOnClickListener {
            submitVote(false)
        }
    }

    override fun onResume() {
        super.onResume()
        chancellor =
            viewModel.getPlayerForReference(viewModel.game.value?.chancellor!!)?.userName
        binding.vetoConsentTitle.text = getString(R.string.veto_consent_title, chancellor)
    }

    private fun submitVote(didAcceptVeto: Boolean) {
        viewModel.activeGamePhase.value = null
        if (didAcceptVeto) {
            viewModel.setGamePhase(GamePhase.nominate_chancellor)
        } else {
            viewModel.setGamePhase(GamePhase.chancellor_discard_policy)
        }
    }
}