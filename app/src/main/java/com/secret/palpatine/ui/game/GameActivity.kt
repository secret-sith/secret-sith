package com.secret.palpatine.ui.game

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.secret.palpatine.R
import com.secret.palpatine.data.model.game.Game
import com.secret.palpatine.data.model.game.GameState
import com.secret.palpatine.data.model.player.Player
import com.secret.palpatine.data.model.player.PlayerListAdapter
import com.secret.palpatine.databinding.ActivityGameBinding
import com.secret.palpatine.ui.BaseActivity

class GameActivity : BaseActivity(), View.OnClickListener {


    private lateinit var binding: ActivityGameBinding
    private lateinit var viewModel: GameViewModel
    private lateinit var game: Game
    private lateinit var playersList: List<Player>

    private var auth: FirebaseAuth = Firebase.auth
    private var canStartGame: Boolean = false
    private var imperialistPolitics: HashMap<Int, ImageView> = hashMapOf()
    private var loyalistPolitics: HashMap<Int, ImageView> = hashMapOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gameId = intent.extras?.getString("gameId")
        binding = ActivityGameBinding.inflate(layoutInflater)
        viewModel = GameViewModelFactory().create(GameViewModel::class.java)
        setContentView(binding.root)
        binding.showLayover.setOnClickListener(this)
        binding.gamePending.btnStart.setOnClickListener(this)
        binding.showPlayers.setOnClickListener(this)
        setProgressBar(binding.progressOverlay.root)

        initPoliticLists()


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
                //Error message here
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
                playersList = playersResult.players
            }
        })

    }

    private fun initGame(game: Game) {


        if (game.host != auth.currentUser?.uid) {

            binding.gamePending.btnStart.visibility = View.INVISIBLE
        }

        when (game.state) {

            GameState.pending -> {

                binding.gamePending.root.visibility = View.VISIBLE
            }

            GameState.finished -> {
                val intent = Intent(this, GameFinishedActivity::class.java).apply {
                    putExtra("gameId", game.id)
                    putExtra("winner", game.winner)
                    putExtra("userId", auth.currentUser?.uid)

                }
                startActivity(intent)
                finish()

            }
        }

        initGameField(game)

    }

    private fun initGameField(game: Game) {
        for (i in 0 until game.imperialPolitics) {
            imperialistPolitics[i + 1]?.setImageDrawable(getDrawable(R.drawable.imperialist_card))
        }
        for (i in 0 until game.loylistPolitics) {
            loyalistPolitics[i + 1]?.setImageDrawable(getDrawable(R.drawable.loyalist_card))
        }
    }


    fun populatePlayerList(players: List<Player>) {

        binding.players.apply {
            layoutManager = LinearLayoutManager(this@GameActivity)
            adapter = PlayerListAdapter(players, context, auth.currentUser!!.uid, false)
        }

        binding.gamePending.playersListOverlay.apply {
            layoutManager = LinearLayoutManager(this@GameActivity)
            adapter = PlayerListAdapter(players, context, auth.currentUser!!.uid,false)
        }
    }

    fun showOverlay() {
        val intent = Intent(this, GameOverlay2Activity::class.java).apply {
            putExtra("gameId", viewModel.currentGame.value)
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
                        "At least 2 Players have to accept to start a game",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            R.id.showPlayers -> {
                if (binding.drawerLayout.isDrawerOpen(Gravity.LEFT)
                ) {
                    binding.drawerLayout.closeDrawer(Gravity.LEFT)
                } else {
                    binding.drawerLayout.openDrawer(Gravity.LEFT)

                }


            }

        }
    }


    private fun initPoliticLists() {
        imperialistPolitics = hashMapOf(
            1 to binding.imperialistPolitic1,
            2 to binding.imperialistPolitic2,
            3 to binding.imperialistPolitic3,
            4 to binding.imperialistPolitic4,
            5 to binding.imperialistPolitic5,
            6 to binding.imperialistPolitic6
        )
        loyalistPolitics = hashMapOf(
            1 to binding.loyalistPolitic1,
            2 to binding.loyalistPolitic2,
            3 to binding.loyalistPolitic3,
            4 to binding.loyalistPolitic4,
            5 to binding.loyalistPolitic5

        )
    }

}
