package com.secret.palpatine.ui.mainmenu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.secret.palpatine.R
import com.secret.palpatine.data.model.invitation.Invite
import kotlinx.android.synthetic.main.activity_main_menu.*
import kotlinx.android.synthetic.main.fragment_invitemenu.*
import com.secret.palpatine.data.model.invitation.InviteListAdapter
import com.secret.palpatine.data.model.dummy_Invites

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class InviteMenuFragment : Fragment() {

   // private val temp_Invites = dummy_Invites

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_invitemenu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        invites_recyclerview.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = InviteListAdapter(sortInvites(dummy_Invites))
        }
    }


    fun sortInvites(invites: List<Invite>): List<Invite> {
        return invites.sortedWith(compareByDescending<Invite>{it.date}.thenBy {it.name})
    }



    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).toolbar.findViewById<TextView>(R.id.mainmenu_toolbar_title).setText(R.string.submenu_invites_toolbar_title)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).toolbar.findViewById<TextView>(R.id.mainmenu_toolbar_title).setText(R.string.submenu_invites_toolbar_title)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }



    companion object {
        fun newInstance(): InviteMenuFragment = InviteMenuFragment()
    }
}
