package com.secret.palpatine.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.secret.palpatine.R
import com.secret.palpatine.data.model.game.GamePhase
import com.secret.palpatine.databinding.FragmentAcceptVetoBinding

class AcceptVetoFragment : Fragment() {
    private lateinit var viewModel: GameViewModel
    private lateinit var binding: FragmentAcceptVetoBinding

    private var chancellor: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAcceptVetoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(GameViewModel::class.java)

        binding.buttonVoteYesVeto.setOnClickListener {
            submitVote(true)
            with(binding.motionLayout) {
                when (currentState) {

                    startState -> {
                        setTransition(R.id.start, R.id.end0)
                    }

                    else -> {
                        setTransition(R.id.end1, R.id.end0)

                    }
                }
                transitionToEnd()
            }
        }

        binding.buttonVoteNoVeto.setOnClickListener {
            submitVote(false)
            with(binding.motionLayout) {
                when (currentState) {

                    startState -> {
                        setTransition(R.id.start, R.id.end1)
                    }

                    else -> {
                        setTransition(R.id.end0, R.id.end1)

                    }
                }
                transitionToEnd()
            }

        }
    }

    override fun onResume() {
        super.onResume()
        chancellor =
            viewModel.getPlayerForReference(viewModel.game.value?.chancellor!!)?.userName
        binding.voteTitle.text = getString(R.string.presidential_power_veto, chancellor)
    }

    private fun submitVote(didAcceptVeto: Boolean) {
    if (didAcceptVeto) {
        viewModel.setGamePhase(GamePhase.nominate_chancellor)
    } else{
        viewModel.setGamePhase(GamePhase.chancellor_discard_policy)
    }

    }
}