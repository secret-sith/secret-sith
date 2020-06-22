package com.secret.palpatine.ui.game

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.secret.palpatine.R
import com.secret.palpatine.databinding.ActivityJoinGameBinding
import com.secret.palpatine.ui.BaseActivity
import com.secret.palpatine.ui.login.afterTextChanged
import com.secret.palpatine.ui.mainmenu.MainMenuActivity
import com.secret.palpatine.ui.signup.SignupActivity
import kotlinx.android.synthetic.main.activity_join_game.view.*

class JoinGameActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityJoinGameBinding
    private lateinit var viewModel: JoinGameViewModel
    private lateinit var gameId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJoinGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setProgressBar(binding.progressOverlay.root)
        val action: String? = intent?.action
        val uri: Uri? = intent?.data
        viewModel = JoinGameViewModelFactory().create(JoinGameViewModel::class.java)

        if (uri != null) {
            try {
                gameId = uri.getQueryParameter("game_id").toString()
                binding.gameID.text = getString(R.string.join_game_id, gameId)
                if (gameId == null) {
                    Toast.makeText(
                        this@JoinGameActivity,
                        "Game Id not found!",
                        Toast.LENGTH_SHORT
                    ).show()
                    goToHome()
                }
                Log.d("Game_ID", gameId)
            } catch (e: Exception) {
            }

        }
        showProgressBar()

        binding.joinButton.setOnClickListener(this)
        //deep links
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                // Get deep link from result (may be null if no link is found)
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                }
                Log.d("Dynamic deep Link", deepLink.toString())

                // ...
            }
        binding.joinButton.isEnabled = false

        viewModel.formResult.observe(this@JoinGameActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            binding.joinButton.isEnabled = loginState.isDataValid && gameId != null

            if (loginState.usernameError != null) {
                binding.playerName.error = getString(loginState.usernameError)
            }

        })

        viewModel.joinGameResult.observe(this@JoinGameActivity, Observer {
            val joinGameResult = it ?: return@Observer

            // disable login button unless both username / password is valid

            if (joinGameResult.success) {

                val intent = Intent(this, GameActivity::class.java).apply {
                    putExtra("gameId", gameId)
                }
                startActivity(intent)
                finish()
            }
            hideProgressBar()
        })


        viewModel.currentUserResult.observe(this@JoinGameActivity, Observer {
            val currentUserResult = it ?: return@Observer

            // disable login button unless both username / password is valid

            if (currentUserResult.user != null) {
                binding.playerName.setText(currentUserResult.user.username)
                if (currentUserResult.user.currentGame != null) {
                    Toast.makeText(
                        this@JoinGameActivity,
                        "You have a active game!",
                        Toast.LENGTH_SHORT
                    ).show()
                    goToHome()
                }

            }
            hideProgressBar()

        })

        viewModel.getCurrentUser()


        binding.playerName.afterTextChanged {
            viewModel.formDataChange(
                binding.playerName.text.toString()
            )
        }
    }

    private fun goToHome() {
        val intent = Intent(this, SignupActivity::class.java).apply {
        }
        startActivity(intent)
        finish()
    }

    override fun onClick(v: View) {
        when (v.id) {

            R.id.join_button -> {
                viewModel.joinGame(gameId, binding.playerName.text.toString())
                showProgressBar()
            }
        }

    }

}
