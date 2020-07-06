package com.secret.palpatine.ui.game

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.secret.palpatine.R
import com.secret.palpatine.data.model.player.PlayerListAdapter
import com.secret.palpatine.databinding.ActivityGameFinishedBinding
import com.secret.palpatine.ui.BaseActivity
import com.secret.palpatine.ui.mainmenu.MainMenuActivity

class GameFinishedActivity : BaseActivity(), View.OnClickListener {


    private lateinit var binding: ActivityGameFinishedBinding
    private lateinit var viewModel: GameViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameFinishedBinding.inflate(layoutInflater)
        val gameId = intent.extras?.getString("gameId")
        val winner = intent.extras?.getString("winner")
        val userId = intent.extras?.getString("userId")


        setContentView(binding.root)
        viewModel = GameViewModelFactory().create(GameViewModel::class.java)
        setProgressBar(binding.progressOverlay.root)


        showProgressBar()
        viewModel.currentGame.observe(this@GameFinishedActivity, Observer {
            val currentGameId = it ?: return@Observer
            viewModel.getPlayers()
            hideProgressBar()

        })
        viewModel.getCurrentGameID(gameId)


        viewModel.playersResult.observe(this@GameFinishedActivity, Observer {
            val playersResult = it ?: return@Observer

            if (playersResult.error != null) {
                Toast.makeText(this, "Error loading players", Toast.LENGTH_LONG).show()

            } else {

                binding.gameEndPlayerList.apply {
                    layoutManager = GridLayoutManager(this@GameFinishedActivity, 2)
                    adapter = PlayerListAdapter(
                        playersResult.players!!,
                        context,
                        userId!!,
                        showMembership = true
                    )
                }
            }
        })

        viewModel.endGameResult.observe(this@GameFinishedActivity, Observer {
            val endGameResult = it ?: return@Observer

            if (endGameResult.success != null && endGameResult.success) {
                val intent = Intent(this, MainMenuActivity::class.java).apply {
                }
                startActivity(intent)
                finish()

            } else {
                Toast.makeText(this, "Could not exit game...", Toast.LENGTH_LONG).show()

            }

        })
        binding.endGameButton.setOnClickListener(this)

        binding.winnerTeam.text = winner

    }

    override fun onClick(view: View) {
        when (view.id) {

            R.id.endGameButton -> {


                viewModel.endGame()

            }

        }
    }
}
