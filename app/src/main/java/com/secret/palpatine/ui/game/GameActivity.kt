package com.secret.palpatine.ui.game

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.secret.palpatine.R
import com.secret.palpatine.data.model.PlayerRole
import com.secret.palpatine.data.model.game.Game
import com.secret.palpatine.data.model.game.GamePhase
import com.secret.palpatine.data.model.game.GameState
import com.secret.palpatine.data.model.player.Player
import com.secret.palpatine.data.model.player.PlayerListAdapter
import com.secret.palpatine.databinding.ActivityGameBinding
import com.secret.palpatine.ui.BaseActivity
import com.secret.palpatine.util.pushFragment

class GameActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityGameBinding
    private lateinit var viewModel: GameViewModel

    private var auth: FirebaseAuth = Firebase.auth
    private var imperialistPolitics: HashMap<Int, ImageView> = hashMapOf()
    private var loyalistPolitics: HashMap<Int, ImageView> = hashMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gameId = intent.extras!!.getString("gameId")!!
        val userId = intent.extras?.getString("userId")

        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)
        viewModel.setGameId(gameId)

        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.showLayover.setOnClickListener(this)
        binding.gamePending.btnStart.setOnClickListener(this)
        binding.showPlayers.setOnClickListener(this)
        setProgressBar(binding.progressOverlay.root)

        initPoliticLists()


        showProgressBar()
        viewModel.game.observe(this@GameActivity, Observer {
            initGame(it)
            hideProgressBar()
        })
        viewModel.gamePhase.observe(this, Observer { phase ->
            when (phase) {
                GamePhase.nominate_chancellor -> {
                    pushFragment(NominateChancellorFragment(), R.id.actionOverlay)
                }
                GamePhase.vote -> {
                    pushFragment(VoteChancellorFragment(), R.id.actionOverlay)
                }
                GamePhase.policy_peek -> {
                    pushFragment(PolicyPeekFragment(), R.id.actionOverlay)
                }
            }
        })
        viewModel.players.observe(this@GameActivity, Observer {
            populatePlayerList(it)
        })

        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
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
            imperialistPolitics[i + 1]?.setImageResource(R.drawable.imperialist_card)
        }
        for (i in 0 until game.loyalistPolitics) {
            loyalistPolitics[i + 1]?.setImageResource(R.drawable.loyalist_card)
        }
    }

    private fun populatePlayerList(players: List<Player>) {
        binding.players.apply {
            layoutManager = LinearLayoutManager(this@GameActivity)
            adapter = PlayerListAdapter(players, context, auth.currentUser!!.uid, false)
        }

        binding.gamePending.playersListOverlay.apply {
            layoutManager = LinearLayoutManager(this@GameActivity)
            adapter = PlayerListAdapter(players, context, auth.currentUser!!.uid, false)
        }

        updateSecretRole(players.findLast { it.user == auth.currentUser!!.uid })
    }

    private fun updateSecretRole(userPlayer: Player?) {

        if (userPlayer != null) {

            when (userPlayer.role) {

                PlayerRole.imperialist -> {
                    binding.yourSecretRole.setImageDrawable(getDrawable(R.drawable.secret_role_imperialist))

                }

                PlayerRole.loyalist -> {
                    binding.yourSecretRole.setImageDrawable(getDrawable(R.drawable.secret_role_loyalist))

                }

                PlayerRole.sith -> {
                    binding.yourSecretRole.setImageDrawable(getDrawable(R.drawable.secret_role_sith))

                }
            }
        }
    }

    private fun toggleOverlay() {
        val oldVisibility = binding.actionOverlay.visibility
        binding.actionOverlay.visibility =
            if (oldVisibility == View.VISIBLE) View.INVISIBLE else View.VISIBLE
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.showLayover -> {
                toggleOverlay()
            }
            R.id.btnStart -> {

                if (true) { // TODO
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
