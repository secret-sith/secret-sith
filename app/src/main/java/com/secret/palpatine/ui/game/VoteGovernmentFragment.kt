package com.secret.palpatine.ui.game

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.secret.palpatine.R
import com.secret.palpatine.util.VOTE
import kotlinx.android.synthetic.main.activity_game.*

class VoteGovernmentFragment : Fragment() {
    private lateinit var viewModel: GameViewModel
    private var presidentialCandidateName: String? = null
    private var chancellorCandidateName: String? = null




    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(GameViewModel::class.java)
    }




    override fun onResume() {
        super.onResume()
        presidentialCandidateName = viewModel.getPlayerForReference(viewModel.game.value?.presidentialCandidate!!)?.userName
        chancellorCandidateName = viewModel.getPlayerForReference(viewModel.game.value?.chancellorCandidate!!)?.userName
        requireView().findViewById<TextView>(R.id.government_election_desc).text = getString( R.string.government_election_desc, presidentialCandidateName, chancellorCandidateName)
    }




    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        return inflater.inflate(R.layout.fragment_vote_chancellor, container, false)
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageButton>(R.id.button_vote_yes).setOnClickListener {
            submitVote(didAcceptGovernment = true) {
                (activity as AppCompatActivity).actionOverlay.visibility = View.INVISIBLE
            }

        }

        view.findViewById<ImageButton>(R.id.button_vote_no).setOnClickListener {
            submitVote(didAcceptGovernment = false) {
                (activity as AppCompatActivity).actionOverlay.visibility = View.INVISIBLE
            }
        }
    }




    private fun submitVote(didAcceptGovernment: Boolean, callback: () -> Unit) {
        // search for yourself
        for (player in viewModel.players.value!!) {
            if (viewModel.userId == player.user) {
                // update your vote
                val playerRef = viewModel.getPlayerRef(player.id)
                playerRef.update(
                    mapOf(
                        VOTE to didAcceptGovernment
                    )
                )
                    .addOnSuccessListener {
                        // check if your vote was the last one missing
                        // return if its not the case
                        for (player_ in viewModel.players.value!!) {
                            if (player_.vote == null) {
                                return@addOnSuccessListener
                            }
                        }
                        // case election is now completed
                        viewModel.handleElectionResult()
                    }
                    .addOnFailureListener {
                        Log.v("VOTE", "vote update failed. -> " + it.stackTrace)
                    }
            }
        }
        // close the overlay activity
        callback.invoke()
    }

}