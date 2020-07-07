package com.secret.palpatine.ui.mainmenu

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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
                val intent = Intent(this, GameActivity::class.java).apply {
                    putExtra("gameId", it)
                    putExtra("userId", viewModel.userId)
                }
                startActivity(intent)
                finish()
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
