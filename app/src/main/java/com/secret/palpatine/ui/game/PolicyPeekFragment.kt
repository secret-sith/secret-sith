package com.secret.palpatine.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.secret.palpatine.data.model.game.GamePhase
import com.secret.palpatine.databinding.GamePolicyPeekFragmentBinding

class PolicyPeekFragment : Fragment() {
    private lateinit var binding: GamePolicyPeekFragmentBinding
    private lateinit var viewModel: GameViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = GamePolicyPeekFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(GameViewModel::class.java)
        viewModel.currentHand.observe(viewLifecycleOwner, Observer { currentHand ->
            binding.policyPeekPolicy0.setImageResource(currentHand[0].type.drawableResource)
            binding.policyPeekPolicy1.setImageResource(currentHand[1].type.drawableResource)
            binding.policyPeekPolicy2.setImageResource(currentHand[2].type.drawableResource)
        })
        binding.close.setOnClickListener {
            viewModel.activeGamePhase.value = null
            viewModel.setGamePhase(GamePhase.nominate_chancellor)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
