package com.secret.palpatine.ui.mainmenu

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.secret.palpatine.R
import com.secret.palpatine.databinding.FragmentMainmenuBinding
import com.secret.palpatine.ui.login.LoginActivity
import kotlinx.android.synthetic.main.activity_main_menu.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class MainMenuFragment : Fragment() {

    var isInSelectionMode: Boolean = false

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = Firebase.auth
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mainmenu, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.mainmenu_invitesbutton).setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        view.findViewById<Button>(R.id.mainmenu_friendsbutton).setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_friendsMenuFragment)
        }

        view.findViewById<Button>(R.id.mainmenu_startbutton).setOnClickListener {
            MainMenuActivity.isInSelectionMode = true
            findNavController().navigate(R.id.action_FirstFragment_to_startGameMenuFragment)
        }

        view.findViewById<Button>(R.id.logout).setOnClickListener {
            auth.signOut()
            val intent = Intent(this.context, LoginActivity::class.java).apply {
            }
            startActivity(intent)
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

    private fun reset(){
        MainMenuActivity.isInSelectionMode = false
        (activity as AppCompatActivity).toolbar.findViewById<TextView>(R.id.mainmenu_toolbar_title).setText(R.string.mainmenu_toolbar_title)
    }
}
