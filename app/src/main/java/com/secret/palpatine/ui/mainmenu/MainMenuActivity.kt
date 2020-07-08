package com.secret.palpatine.ui.mainmenu

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.secret.palpatine.R
import com.secret.palpatine.databinding.ActivityMainMenuBinding
import com.secret.palpatine.ui.BaseActivity
import com.secret.palpatine.ui.game.GameActivity

import kotlinx.android.synthetic.main.activity_main_menu.*

class MainMenuActivity : BaseActivity() {
    private lateinit var viewModel: MainMenuViewModel
    private lateinit var binding: ActivityMainMenuBinding

    companion object {
        var isInSelectionMode = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        viewModel = ViewModelProvider(this).get(MainMenuViewModel::class.java)

        binding.progressOverlay.root.visibility = View.VISIBLE
        viewModel.getCurrentUsersGameId().addOnSuccessListener {
                if (it != null) {

                    val bundle = bundleOf("gameId" to it, "userId" to viewModel.userId)
                    findNavController(R.id.nav_host_fragment).navigate(R.id.action_mainMenuFragment_to_gamePendingFragment, bundle)
                }

            binding.progressOverlay.root.visibility = View.GONE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        return true
    }

}
