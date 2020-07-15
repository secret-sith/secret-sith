package com.secret.palpatine.ui.game

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
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
import com.secret.palpatine.data.model.player.PlayerGameListAdapter
import com.secret.palpatine.databinding.ActivityGameBinding
import com.secret.palpatine.ui.BaseActivity
import com.secret.palpatine.util.pushFragment
import com.secret.palpatine.util.removeFragment

class GameActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityGameBinding
    private lateinit var viewModel: GameViewModel

    private var auth: FirebaseAuth = Firebase.auth

    private lateinit var evilPolicies: List<ImageView>
    private lateinit var goodPolicies: List<ImageView>
    private lateinit var electionTracker: List<ImageView>

    private lateinit var playerListAdapter: PlayerGameListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gameId = intent.extras!!.getString("gameId")!!

        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)
        viewModel.setGameId(gameId)

        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.showLayover.setOnClickListener(this)
        binding.showPlayers.setOnClickListener(this)
        setProgressBar(binding.progressOverlay.root)

        playerListAdapter =
            PlayerGameListAdapter(listOf(), context = this, currentUserId = auth.currentUser!!.uid)

        initLists()


        showProgressBar()
        viewModel.game.observe(this@GameActivity, Observer {
            initGame(it)
            hideProgressBar()
        })
        viewModel.activeGamePhase.observe(this, Observer { phase ->
            val fragment = when (phase) {
                GamePhase.nominate_chancellor -> NominateChancellorFragment()
                GamePhase.vote -> VoteFragment()
                GamePhase.president_discard_policy -> PresidentDiscardPolicyFragment()
                GamePhase.chancellor_discard_policy -> ChancellorDiscardPolicyFragment()
                GamePhase.policy_peek -> PolicyPeekFragment()
                GamePhase.kill -> ExecutePlayerFragment()
                GamePhase.president_accept_veto -> VetoConsentFragment()
                null -> null
            }
            if (fragment != null) {
                pushFragment(fragment, R.id.actionOverlay)
                binding.actionOverlay.visibility = View.VISIBLE
            } else {
                removeFragment(R.id.actionOverlay)
                binding.actionOverlay.visibility = View.INVISIBLE
            }
        })
        viewModel.players.observe(this@GameActivity, Observer {
            Log.d("Players", it.toString())
            populatePlayerList(it)
        })

        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_IMMERSIVE or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    private fun initLists() {
        evilPolicies = listOf(
            binding.evilPolicy0,
            binding.evilPolicy1,
            binding.evilPolicy2,
            binding.evilPolicy3,
            binding.evilPolicy4,
            binding.evilPolicy5
        )
        goodPolicies = listOf(
            binding.goodPolicy0,
            binding.goodPolicy1,
            binding.goodPolicy2,
            binding.goodPolicy3,
            binding.goodPolicy4
        )
        electionTracker = listOf(
            binding.electionTracker0,
            binding.electionTracker1,
            binding.electionTracker2,
            binding.electionTracker3
        )
    }

    private fun initGame(game: Game) {

        playerListAdapter.game = game
        playerListAdapter.notifyDataSetChanged()
        when (game.state) {

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
        for (i in evilPolicies.indices) {
            evilPolicies[i].setImageResource(if (i < game.imperialPolitics) R.drawable.policy_evil else 0)
        }
        for (i in goodPolicies.indices) {
            goodPolicies[i].setImageResource(if (i < game.loyalistPolitics) R.drawable.policy_good else 0)
        }
        setFailedGovernments(game.failedGovernments)
    }

    private fun setFailedGovernments(failedGovernments: Int) {
        for (i in electionTracker.indices) {
            electionTracker[i].visibility =
                if (i == failedGovernments) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun populatePlayerList(players: List<Player>) {
        playerListAdapter.setItems(players)
        binding.players.apply {
            layoutManager = LinearLayoutManager(this@GameActivity)
            adapter = playerListAdapter
        }

        updateRole(players.findLast { it.user == auth.currentUser!!.uid })
    }

    private fun updateRole(userPlayer: Player?) {
        if (userPlayer != null) {
            binding.role.setImageResource(
                when (userPlayer.role) {
                    PlayerRole.imperialist -> R.drawable.role_evil_2
                    PlayerRole.loyalist -> R.drawable.role_good_3
                    PlayerRole.sith -> R.drawable.role_evil_leader
                }
            )
        }
    }

    private fun toggleOverlay() {
        if (viewModel.activeGamePhase.value == null) return
        val oldVisibility = binding.actionOverlay.visibility
        binding.actionOverlay.visibility =
            if (oldVisibility == View.VISIBLE) View.INVISIBLE else View.VISIBLE
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.showLayover -> {
                toggleOverlay()
            }
            R.id.showPlayers -> {
                if (binding.drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    binding.drawerLayout.closeDrawer(Gravity.LEFT)
                } else {
                    binding.drawerLayout.openDrawer(Gravity.LEFT)
                }
            }
        }
    }
}
