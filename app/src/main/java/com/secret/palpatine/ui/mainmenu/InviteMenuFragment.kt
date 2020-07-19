package com.secret.palpatine.ui.mainmenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.secret.palpatine.R
import com.secret.palpatine.data.model.invitation.Invite
import com.secret.palpatine.data.model.invitation.InviteListAdapter
import kotlinx.android.synthetic.main.activity_main_menu.*
import kotlinx.android.synthetic.main.fragment_invitemenu.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class InviteMenuFragment : Fragment(), InviteListAdapter.AcceptInviteListener {

    /**
     * MainMenuViewModel object that holds this player's invites
     */
    private lateinit var viewModel: MainMenuViewModel


    /**
     * Override of onCreate. Initial set of the MainMenuViewModel
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainMenuViewModel::class.java)
        retainInstance = true
    }

    /**
     * Override of onCreateView. Sets the correct fragment layout
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_invitemenu, container, false)
    }

    /**
     * Override of onViewCreated. Observes the current user's invites and pushes the available
     * invites to the Recyclerview
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        invitesLoading.visibility = View.VISIBLE

        viewModel.inviteListResult.observe(viewLifecycleOwner, Observer {
            val inviteListResult = it ?: return@Observer
            invitesLoading.visibility = View.GONE

            if (inviteListResult.error != null) {
            }
            if (inviteListResult.success != null) {
                invites_recyclerview.apply {
                    layoutManager = LinearLayoutManager(activity)
                    adapter = InviteListAdapter(
                        sortInvites(inviteListResult.success),
                        this@InviteMenuFragment
                    )
                }
            }
        })

        viewModel.getInvites()
    }


    /**
     * Sorts a list of invites by their timestamp
     *
     * @param invites: the list of invites to be sorted
     */
    private fun sortInvites(invites: List<Invite>): List<Invite> {
        return invites.sortedWith(compareByDescending<Invite> { it.timestamp }.thenBy { it.name })
    }


    /**
     * Override of onStart. Sets the toolbar header text and limits backnavigation on home pressed
     * to one layer
     */
    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).toolbar.findViewById<TextView>(R.id.mainmenu_toolbar_title)
            .setText(R.string.submenu_invites_toolbar_title)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * Override of onResume. Sets the toolbar header text and limits backnavigation on home pressed
     * to one layer
     */
    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).toolbar.findViewById<TextView>(R.id.mainmenu_toolbar_title)
            .setText(R.string.submenu_invites_toolbar_title)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * Accepts an invite and switches to the game-pending screen on success.
     *
     * @param data: the invite to accept
     */
    override fun onAccept(data: Invite) {
        invitesLoading.visibility = View.VISIBLE

        viewModel.acceptInvite(data).addOnSuccessListener {
            invitesLoading.visibility = View.GONE
            val bundle = bundleOf(Pair("gameId", data.gameId))
            findNavController().navigate(
                R.id.action_inviteMenuFragment_to_gamePendingFragment,
                bundle
            )
        }.addOnFailureListener {
            invitesLoading.visibility = View.GONE
            Toast.makeText(context, "Could not join Game!", Toast.LENGTH_SHORT).show()

        }
    }

    companion object {
        fun newInstance(): InviteMenuFragment = InviteMenuFragment()
    }
}
