package com.secret.palpatine.ui.game.ui.gameoverlay2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.secret.palpatine.R
import com.secret.palpatine.data.model.game.Game
import com.secret.palpatine.data.model.player.Player
import com.secret.palpatine.data.model.player.SelectPlayerListAdapter
import com.secret.palpatine.databinding.GameNominateChancellorFragmentBinding
import com.secret.palpatine.databinding.GamePolicyPeekFragmentBinding
import kotlinx.android.synthetic.main.activity_game.view.*

class PolicyPeekFragment : Fragment() {
    private lateinit var binding: GamePolicyPeekFragmentBinding
    private lateinit var viewModel: GameOverlay2ViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = GamePolicyPeekFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(GameOverlay2ViewModel::class.java)
        viewModel.currentHand.observe(viewLifecycleOwner, Observer { currentHand ->
            binding.policyPeekPolicy0.setImageResource(currentHand[0].type.drawableResource)
            binding.policyPeekPolicy1.setImageResource(currentHand[1].type.drawableResource)
            binding.policyPeekPolicy2.setImageResource(currentHand[2].type.drawableResource)
        })
        binding.close.setOnClickListener {
            viewModel.nominateNextPresident()
            requireActivity().finish()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
