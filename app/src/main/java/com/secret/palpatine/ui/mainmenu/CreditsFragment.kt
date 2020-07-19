package com.secret.palpatine.ui.mainmenu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.secret.palpatine.R
import kotlinx.android.synthetic.main.activity_main_menu.*

/**
 * Fragment class for displaying the credits for the game idea and the used open-source layout material
 */
class CreditsFragment : Fragment() {

    /**
     * Override of onCreateView. Inflates the fragment with the correct layout
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_credits, container, false)
    }

    /**
     * Override of onStart. Sets the toolbar header text and limits backnavigation on home pressed
     * to one layer
     */
    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).toolbar.findViewById<TextView>(R.id.mainmenu_toolbar_title)
            .setText(R.string.submenu_credits_toolbar_title)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * Override of onResume. Sets the toolbar header text and limits backnavigation on home pressed
     * to one layer
     */
    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).toolbar.findViewById<TextView>(R.id.mainmenu_toolbar_title)
            .setText(R.string.submenu_credits_toolbar_title)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

}