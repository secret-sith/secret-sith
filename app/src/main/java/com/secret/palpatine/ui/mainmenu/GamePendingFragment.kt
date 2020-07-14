package com.secret.palpatine.ui.mainmenu

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.secret.palpatine.R
import com.secret.palpatine.data.model.game.Game
import com.secret.palpatine.data.model.game.GameState
import com.secret.palpatine.data.model.player.Player
import com.secret.palpatine.data.model.player.PlayerListAdapter
import com.secret.palpatine.ui.BaseActivity
import com.secret.palpatine.ui.game.GameActivity
import com.secret.palpatine.ui.game.GameFinishedActivity
import com.secret.palpatine.ui.game.GameViewModel
import kotlinx.android.synthetic.main.fragment_game_pending.*

/**
 * Fragment to show a game which is still pending with the invited players
 */
class GamePendingFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var list: RecyclerView
    private lateinit var viewModel: GameViewModel
    private lateinit var startButton: Button
    private var gameId: String? = null
    private var canStartGame: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = Firebase.auth
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)

        return inflater.inflate(R.layout.fragment_game_pending, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val myClipboard: ClipboardManager =
            activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        list = view.findViewById(R.id.players_list_overlay)
        startButton = view.findViewById(R.id.btnStart)

        (activity as BaseActivity).supportActionBar!!.hide()
        (activity as BaseActivity).showProgressBar()
        startButton.setOnClickListener {
            if (canStartGame) { // TODO
                viewModel.start()
                val intent = Intent(context, GameActivity::class.java).apply {
                    putExtra("gameId", gameId)
                }
                startActivity(intent)
                activity?.finish()

            } else {
                Toast.makeText(
                    context,
                    "Exactly 5 Players have to accept to start a game",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        viewModel.players.observe(viewLifecycleOwner, Observer {
            Log.d("Players", it.toString())
            populatePlayerList(it)
        })

        viewModel.game.observe(viewLifecycleOwner, Observer {
            if(it != null){
                init(it)
            }
            (activity as BaseActivity).hideProgressBar()
        })
        viewModel.canStartGame.observe(viewLifecycleOwner, Observer {
            val result = it ?: return@Observer

            canStartGame = result
        })

        viewModel.getCurrentUsersGameId().addOnSuccessListener {
            val argGameId = arguments?.getString("gameId");
            if (it != null) {
                loadGame(it)
            } else if (argGameId != null) {
                loadGame(argGameId)

            } else {
                findNavController().navigate(R.id.action_gamePendingFragment_to_mainMenuFragment)
                (activity as BaseActivity).hideProgressBar()
            }

        }.addOnFailureListener {
            Log.e("Load user error", it.message)
            (activity as BaseActivity).hideProgressBar()
            Toast.makeText(
                context,
                "Error while loading your Profile - please try again",
                Toast.LENGTH_SHORT
            )
                .show()
        }
        txtInviteUrl.setOnClickListener {

            val textToCopy = txtInviteUrl.text
            val clip = ClipData.newPlainText("RANDOM UUID", textToCopy)
            myClipboard.setPrimaryClip(clip)
            Toast.makeText(context, R.string.copy_succcess, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        reset()
    }

    override fun onResume() {
        super.onResume()
        reset()
    }

    private fun reset() {
    }

    private fun populatePlayerList(players: List<Player>) {
        list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = PlayerListAdapter(players, context, auth.currentUser!!.uid, false)
        }

    }

    private fun loadGame(gameId: String) {
        viewModel.loadGameAndPlayersForPendingState(gameId)
        this.gameId = gameId
        txtInviteUrl.text = getString(R.string.invite_url_template, gameId)
    }

    private fun init(game: Game) {
        if (game.host != auth.currentUser?.uid) {
            startButton.visibility = View.INVISIBLE
        }

        if (game.state == GameState.started) {
            val intent = Intent(context, GameActivity::class.java).apply {
                putExtra("gameId", game.id)
                putExtra("userId", auth.currentUser?.uid)
            }
            startActivity(intent)
            activity?.finish()
        } else if (game.state == GameState.finished) {
            val intent = Intent(context, GameFinishedActivity::class.java).apply {
                putExtra("gameId", game.id)
                putExtra("userId", auth.currentUser?.uid)
                putExtra("winner", game.winner)
            }
            startActivity(intent)
            activity?.finish()
        }

    }

}
