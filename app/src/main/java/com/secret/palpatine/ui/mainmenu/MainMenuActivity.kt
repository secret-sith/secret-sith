package com.secret.palpatine.ui.mainmenu

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.secret.palpatine.R
import com.secret.palpatine.databinding.ActivityMainMenuBinding
import com.secret.palpatine.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_main_menu.*

/**
 * Activity managing the MainMenu of the game
 */
class MainMenuActivity : BaseActivity() {

    /**
     * MainMenuViewModel for the communication with firebase
     */
    private lateinit var viewModel: MainMenuViewModel

    /**
     * binding for the layout
     */
    private lateinit var binding: ActivityMainMenuBinding

    /**
     * companion object for determining if the playerlist is just displayed or used for selection
     */
    companion object {
        var isInSelectionMode = false
    }

    /**
     * Override of onCreate. Inflates the layout and sets the viewModel. Redirects to the GamePending
     * fragment in case the app got opened by an invitelink
     */
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

    /**
     * Override of onSupportNavigateUp. Navigates back by one layer
     */
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        return true
    }

}
