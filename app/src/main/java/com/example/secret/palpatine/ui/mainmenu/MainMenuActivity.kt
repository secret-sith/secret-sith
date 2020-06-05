package com.example.secret.palpatine.ui.mainmenu

import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.example.secret.palpatine.R

import kotlinx.android.synthetic.main.activity_main_menu.*
import kotlin.properties.Delegates.observable

class MainMenuActivity : AppCompatActivity() {

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

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        return true
    }

}
