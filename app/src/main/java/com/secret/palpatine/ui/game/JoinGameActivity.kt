package com.secret.palpatine.ui.game

import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.secret.palpatine.databinding.ActivityJoinGameBinding
import com.secret.palpatine.ui.BaseActivity

class JoinGameActivity : BaseActivity() {

    private lateinit var binding: ActivityJoinGameBinding
    private lateinit var gameId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJoinGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val action: String? = intent?.action
        val uri: Uri? = intent?.data

        if (uri != null) {
            try {
                gameId = uri.getQueryParameter("game_id").toString()
                Log.d("Game_ID", gameId)
            } catch (e: Exception) {
            }

        }
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
    }


}
