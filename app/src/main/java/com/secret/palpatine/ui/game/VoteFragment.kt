package com.secret.palpatine.ui.game

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.secret.palpatine.R
import com.secret.palpatine.databinding.GameVoteFragmentBinding
import com.secret.palpatine.util.VOTE

class VoteFragment : Fragment() {
    private lateinit var viewModel: GameViewModel
    private lateinit var binding: GameVoteFragmentBinding

    private var presidentialCandidateName: String? = null
    private var chancellorCandidateName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = GameVoteFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(GameViewModel::class.java)

        binding.buttonVoteYes.setOnClickListener {
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

        binding.buttonVoteNo.setOnClickListener {
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
        presidentialCandidateName =
            viewModel.getPlayerForReference(viewModel.game.value?.presidentialCandidate!!)?.userName
        chancellorCandidateName =
            viewModel.getPlayerForReference(viewModel.game.value?.chancellorCandidate!!)?.userName
        binding.voteTitle.text = getString(
            R.string.government_election_desc,
            presidentialCandidateName,
            chancellorCandidateName
        )
    }

    private fun submitVote(didAcceptGovernment: Boolean) {

        val player = viewModel.player.value!!
        // update your vote
        val playerRef = viewModel.getPlayerRef(player.id)
        playerRef.update(VOTE, didAcceptGovernment).addOnSuccessListener {
            // check if your vote was the last one missing
            // return if its not the case
            for (player_ in viewModel.players.value!!) {
                if (player_.vote == null) {
                    return@addOnSuccessListener
                }
            }
            // case election is now completed
            viewModel.activeGamePhase.value = null
            viewModel.handleElectionResult()
        }.addOnFailureListener {
            Log.v("VOTE", "vote update failed. -> " + it.stackTrace)
        }
    }
}