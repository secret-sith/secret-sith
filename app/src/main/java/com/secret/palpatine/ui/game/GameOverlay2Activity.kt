package com.secret.palpatine.ui.game

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.secret.palpatine.R
import com.secret.palpatine.data.model.game.GamePhase
import com.secret.palpatine.ui.game.ui.gameoverlay2.GameOverlay2ViewModel
import com.secret.palpatine.ui.game.ui.gameoverlay2.NominateChancellorFragment

class GameOverlay2Activity : AppCompatActivity() {
    private lateinit var viewModel: GameOverlay2ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_overlay2_activity)
        val gameId: String = intent.extras!!.getString("gameId")!!
        viewModel = ViewModelProvider(this).get(GameOverlay2ViewModel::class.java)
        viewModel.game.observe(this, Observer { game ->
            when (game.phase) {
                GamePhase.nominate_chancellor -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, NominateChancellorFragment.newInstance())
                        .commitNow()
                }
            }
            Toast.makeText(
                this,
                game.phase.toString(),
                Toast.LENGTH_SHORT
            ).show()
        })
        viewModel.setGameId(gameId)

    }
}
