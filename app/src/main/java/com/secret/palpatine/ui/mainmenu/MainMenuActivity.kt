package com.secret.palpatine.ui.mainmenu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.secret.palpatine.R
import com.secret.palpatine.ui.BaseActivity
import com.secret.palpatine.ui.game.GameActivity
import com.secret.palpatine.ui.login.LoginActivity

import kotlinx.android.synthetic.main.activity_main_menu.*
import kotlin.properties.Delegates.observable

class MainMenuActivity : BaseActivity() {

    companion object {
        var isInSelectionMode = false
        var startGame: Boolean by observable(false) {_, oldValue, newValue ->
            onGameStart?.invoke(oldValue, newValue)
        }
        var onGameStart: ((Boolean,Boolean) -> Unit)? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayShowTitleEnabled(false)

        MainMenuActivity.onGameStart = { _, _ ->
            startGame()
        }

    }

    private fun startGame(){
        Log.v("v","game would start now")
        startActivity(Intent(this, GameActivity::class.java))
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        return true
    }

}
