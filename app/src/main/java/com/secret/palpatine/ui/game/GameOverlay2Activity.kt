package com.secret.palpatine.ui.game

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.secret.palpatine.R
import com.secret.palpatine.data.model.game.GamePhase
import com.secret.palpatine.ui.game.ui.gameoverlay2.GameOverlay2ViewModel
import com.secret.palpatine.ui.game.ui.gameoverlay2.NominateChancellorFragment
import com.secret.palpatine.util.pushFragment

class GameOverlay2Activity : AppCompatActivity() {
    private lateinit var viewModel: GameOverlay2ViewModel
    public var userId: String? = null
    public var userName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_overlay2_activity)

        userId = intent.extras!!.getString("userId")
        userName = intent.extras!!.getString("userName")

        val gameId: String = intent.extras!!.getString("gameId")!!
        viewModel = ViewModelProvider(this).get(GameOverlay2ViewModel::class.java)


        viewModel.game.observe(this, Observer { game ->
            when (game.phase) {
                GamePhase.nominate_chancellor -> {
                    pushFragment(NominateChancellorFragment(), R.id.container)
                }
                GamePhase.vote -> {
                    pushFragment(VoteChancellorFragment(), R.id.container)
                }
            }

            Toast.makeText(
                this,
                game.phase.toString(),
                Toast.LENGTH_SHORT
            ).show()
        })

        viewModel.setGameId(gameId)
        viewModel.userId = userId
        viewModel.userName = userName

        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)

    }


    /*
    fun manageButtonClickability(){
        when(viewModel.game.value!!.phase){
            GamePhase.vote -> {
                viewModel.thisPlayer?.vote == null
            }
            else -> { /* TODO */ }
        }
    }
    */

}
