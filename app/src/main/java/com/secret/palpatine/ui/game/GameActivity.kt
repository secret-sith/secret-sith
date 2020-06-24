package com.secret.palpatine.ui.game

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.secret.palpatine.R
import com.secret.palpatine.data.model.friends.friendgroup.FriendGroupAdapter
import com.secret.palpatine.data.model.game.Game
import com.secret.palpatine.data.model.game.GameState
import com.secret.palpatine.data.model.player.Player
import com.secret.palpatine.data.model.player.PlayerListAdapter
import com.secret.palpatine.databinding.ActivityGameBinding
import com.secret.palpatine.databinding.ActivitySignupBinding
import com.secret.palpatine.ui.BaseActivity
import com.secret.palpatine.ui.login.LoginActivity
import kotlinx.android.synthetic.main.fragment_friendsmenu.*

class GameActivity : BaseActivity(), View.OnClickListener {


    private lateinit var binding: ActivityGameBinding
    private lateinit var viewModel: GameViewModel
    private lateinit var game: Game
    private var auth: FirebaseAuth = Firebase.auth
    private var canStartGame: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gameId = intent.extras?.getString("gameId")
        binding = ActivityGameBinding.inflate(layoutInflater)
        viewModel = GameViewModelFactory().create(GameViewModel::class.java)
        setContentView(binding.root)
        binding.showLayover.setOnClickListener(this)
        binding.gamePending.btnStart.setOnClickListener(this)
        setProgressBar(binding.progressOverlay.root)

        showProgressBar()
        viewModel.currentGame.observe(this@GameActivity, Observer {
            val currentGameId = it ?: return@Observer
            viewModel.getGame();
            viewModel.getPlayers()

        })
        val reference = viewModel.getCurrentGameID(gameId)

        viewModel.currentGameResult.observe(this@GameActivity, Observer {
            val currentGameResult = it ?: return@Observer

            reference?.remove()
            if (currentGameResult.error != null) {
                Toast.makeText(this, "Error loading Game", Toast.LENGTH_LONG).show()
            } else if (currentGameResult.game != null) {
                game = currentGameResult?.game
                initGame(currentGameResult?.game)
            } else {

            }
            hideProgressBar()
        })

        viewModel.canStartGame.observe(this@GameActivity, Observer {
            val result = it ?: return@Observer

            canStartGame = result
        })

        viewModel.playersResult.observe(this@GameActivity, Observer {
            val playersResult = it ?: return@Observer

            if (playersResult.error != null) {
                Toast.makeText(this, "Error loading players", Toast.LENGTH_LONG).show()

            } else {
                populatePlayerList(playersResult.players!!)
            }
        })

    }

    fun initGame(game: Game) {


        if (game.host != auth.currentUser?.uid) {

            binding.gamePending.btnStart.visibility = View.INVISIBLE
        }

        when (game!!.state) {

            GameState.pending -> {

                binding.gamePending.root.visibility = View.VISIBLE
            }
        }

    }

    fun populatePlayerList(players: List<Player>) {

        binding.players.apply {
            layoutManager = LinearLayoutManager(this@GameActivity)
            adapter = PlayerListAdapter(players, context, auth.currentUser!!.uid)
        }

        binding.gamePending.playersListOverlay.apply {
            layoutManager = LinearLayoutManager(this@GameActivity)
            adapter = PlayerListAdapter(players, context, auth.currentUser!!.uid)
        }
    }

    fun showOverlay() {
        val intent = Intent(this, GameOverlayActivity::class.java).apply {
            putExtra("game", game)
        }
        startActivity(intent)
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.showLayover -> {
                showOverlay()
            }
            R.id.btnStart -> {

                if (canStartGame) {
                    binding.gamePending.root.visibility = View.GONE
                    viewModel.start()
                } else {
                    Toast.makeText(
                        this@GameActivity,
                        "At least 5 Players have to accept to start a game",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }
    }


}
