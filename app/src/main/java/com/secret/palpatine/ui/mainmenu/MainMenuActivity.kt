package com.secret.palpatine.ui.mainmenu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.secret.palpatine.R
import com.secret.palpatine.databinding.ActivityMainMenuBinding
import com.secret.palpatine.ui.BaseActivity
import com.secret.palpatine.ui.game.GameActivity
import com.secret.palpatine.ui.login.LoginActivity

import kotlinx.android.synthetic.main.activity_main_menu.*
import kotlinx.android.synthetic.main.fragment_friendsmenu.*
import kotlin.properties.Delegates.observable

class MainMenuActivity : BaseActivity() {

    private lateinit var viewModel: MainMenuViewModel
    private lateinit var binding: ActivityMainMenuBinding

    companion object {
        var isInSelectionMode = false
        var startGame: Boolean by observable(false) { _, oldValue, newValue ->
            onGameStart?.invoke(oldValue, newValue)
        }
        var onGameStart: ((Boolean, Boolean) -> Unit)? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        viewModel = MainMenuViewModelFactory().create(MainMenuViewModel::class.java);

        MainMenuActivity.onGameStart = { _, _ ->
            startGame()
        }

        binding.progressOverlay.root.visibility = View.VISIBLE


        viewModel.currentGameResult.observe(this@MainMenuActivity, Observer {
            val currentGameResult = it ?: return@Observer

            if (currentGameResult.gameId != null) {
                val intent = Intent(this, GameActivity::class.java).apply {
                    putExtra("gameId", currentGameResult.gameId)
                }
                startActivity(intent)
                finish()
            }
            binding.progressOverlay.root.visibility = View.GONE

        })

        viewModel.checkCurrentUsersGame()
    }

    private fun startGame() {
        Log.v("v", "game would start now")
        startActivity(Intent(this, GameActivity::class.java))
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        return true
    }

}
