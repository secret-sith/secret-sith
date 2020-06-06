package com.secret.palpatine.ui.game

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.secret.palpatine.R

import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        setSupportActionBar(toolbar)

    }

}
