package com.secret.palpatine.ui.game

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.secret.palpatine.R
import com.secret.palpatine.databinding.ActivityGameOverlayBinding
import com.secret.palpatine.util.replaceFragment
import kotlinx.android.synthetic.main.activity_main_menu.*

class GameOverlayActivity : AppCompatActivity(), View.OnClickListener {


    private lateinit var binding: ActivityGameOverlayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameOverlayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(toolbar)

        binding.gameActivityParent.setOnClickListener(this)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        pushFragment(SelectChancellorFragment(), R.id.overlay_frame_layout)

        Toast.makeText(
            this@GameOverlayActivity,
            "push select chancellor",
            Toast.LENGTH_SHORT
        ).show()
    }




    override fun onStart() {
        super.onStart()
        this.getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true);

    }

    override fun onResume() {
        super.onResume()
        this.getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true);

    }

    override fun onSupportNavigateUp(): Boolean {
        finish() // close this activity as oppose to navigating up
        return false
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.game_activity_parent -> {
                finish()
            }
        }
    }

    public fun push_select_chancellor(){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.overlay_frame_layout, SelectChancellorFragment())
        transaction.commit()
    }
}
