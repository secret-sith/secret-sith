package com.secret.palpatine.ui.mainmenu

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.secret.palpatine.R
import com.secret.palpatine.ui.login.LoginActivity

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class MainMenuFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = Firebase.auth
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mainmenu, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.mainmenu_invitesbutton).setOnClickListener {
            MainMenuActivity.isInSelectionMode = false

            findNavController().navigate(R.id.action_mainMenuFragment_to_inviteMenuFragment)
        }

        view.findViewById<Button>(R.id.mainmenu_friendsbutton).setOnClickListener {
            MainMenuActivity.isInSelectionMode = false

            findNavController().navigate(R.id.action_mainMenuFragment_to_friendsMenuFragment)
        }

        view.findViewById<Button>(R.id.mainmenu_startbutton).setOnClickListener {
            MainMenuActivity.isInSelectionMode = true
            findNavController().navigate(R.id.action_mainMenuFragment_to_startGameMenuFragment)
        }

        view.findViewById<Button>(R.id.logout).setOnClickListener {
            auth.signOut()
            val intent = Intent(this.context, LoginActivity::class.java).apply {
            }
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.mainmenu_manualbutton).setOnClickListener {
            val url =
                Uri.parse("https://docs.google.com/viewerng/viewer?url=https://tinyurl.com/ydbvvlrq")
            val intent = Intent(Intent.ACTION_VIEW, url)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.mainmenu_creditsbutton).setOnClickListener {
            MainMenuActivity.isInSelectionMode = true
            findNavController().navigate(R.id.action_mainMenuFragment_to_creditsFragment)
        }
    }

    override fun onStart() {
        super.onStart()
        reset()
    }

    override fun onResume() {
        super.onResume()
        reset()
    }

    private fun reset() {
        MainMenuActivity.isInSelectionMode = false
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar?.show()
    }
}
