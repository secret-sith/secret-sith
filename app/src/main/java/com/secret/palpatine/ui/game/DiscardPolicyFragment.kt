package com.secret.palpatine.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.secret.palpatine.data.model.game.GamePhase
import com.secret.palpatine.databinding.GameDiscardPolicyFragmentBinding

class DiscardPolicyFragment : Fragment() {
    private lateinit var binding: GameDiscardPolicyFragmentBinding
    private lateinit var viewModel: GameViewModel
    private lateinit var detector: GestureDetectorCompat

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = GameDiscardPolicyFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(GameViewModel::class.java)
        val discardPolicyBindings = listOf(
            binding.discardPolicy0,
            binding.discardPolicy1,
            binding.discardPolicy2
        )
        viewModel.currentHand.observe(viewLifecycleOwner, Observer { currentHand ->
            for (i in 0 until 3) {
                val policy = currentHand.getOrNull(i)
                val binding = discardPolicyBindings[i]
                if (policy != null) {
                    binding.setImageResource(policy.type.drawableResource)
                    binding.visibility = View.VISIBLE
                } else {
                    binding.visibility = View.GONE
                }
            }
        })
        for (i in 0 until 3) {
            discardPolicyBindings[i].setOnClickListener {
                viewModel.discardPolicy(i)
            }
        }
    }
}