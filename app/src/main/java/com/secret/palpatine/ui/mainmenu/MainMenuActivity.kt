package com.secret.palpatine.ui.mainmenu

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.secret.palpatine.R
import com.secret.palpatine.databinding.ActivityMainMenuBinding
import com.secret.palpatine.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_main_menu.*

class MainMenuActivity : BaseActivity() {
    private lateinit var viewModel: MainMenuViewModel
    private lateinit var binding: ActivityMainMenuBinding

    companion object {
        var isInSelectionMode = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setProgressBar(binding.progressOverlay.root)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        viewModel = ViewModelProvider(this).get(MainMenuViewModel::class.java)

        // redirect to game-pending screen in case someone joined through invite link
        val cameByInvite = intent.extras?.getBoolean("comesByInviteLink")
        if (cameByInvite == true) findNavController(R.id.nav_host_fragment).navigate(R.id.gamePendingFragment)


    }

    override fun onResume() {
        super.onResume()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        return true
    }

}
