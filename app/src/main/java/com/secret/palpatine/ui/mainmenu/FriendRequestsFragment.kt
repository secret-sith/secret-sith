package com.secret.palpatine.ui.mainmenu

import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.secret.palpatine.R
import com.secret.palpatine.data.model.friends.friend.request.FriendRequest
import com.secret.palpatine.data.model.friends.friend.request.FriendRequestsListAdapter
import kotlinx.android.synthetic.main.activity_main_menu.*
import kotlinx.android.synthetic.main.fragment_friendsmenu.*

/**
 * Created by Florian Fuchs on 09.06.2020.
 */
class FriendRequestsFragment : Fragment(), FriendRequestsListAdapter.FriendRequestAcceptListener {

    /**
     * MainMenuViewModel object for communicating with firebase
     */
    private lateinit var viewModel: MainMenuViewModel

    /**
     * Override of onCreate. Inital set of the MainMenuViewModel
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainMenuViewModel::class.java)
        retainInstance = true
    }

    /**
     * Override of onCreateView. Inflates the fragment with the correct layout
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friendsrequestmenu, container, false)

    }

    override fun onAccept(data: FriendRequest) {
        viewModel.acceptFriendRequest(data.id)
    }

    /**
     * Override of onViewCreated. Observes the open friend requests and populates the recyclerview
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loading.visibility = View.VISIBLE

        viewModel.friendsRequestResult.observe(viewLifecycleOwner, Observer {
            val friendListResult = it ?: return@Observer
            loading.visibility = View.GONE

            if (friendListResult.error != null) {
            }
            if (friendListResult.success != null) {
                populateList(friendListResult.success)
            }
        })

        viewModel.acceptFriendRequestResult.observe(viewLifecycleOwner, Observer {
            val loginResult = it ?: return@Observer

            if (loginResult.error != null) {
                Toast.makeText(context, R.string.error_accept_friend, Toast.LENGTH_SHORT)
            }
            if (loginResult.success) {
                Toast.makeText(context, R.string.success_accept_friend, Toast.LENGTH_SHORT)
            }
        })


        viewModel.getUserFriendRequests()
    }


    /**
     * Function to populate the recyclerview with a given list of requests
     *
     * @param requests: list of requests to display
     */
    private fun populateList(requests: List<FriendRequest>) {
        val context = (activity as AppCompatActivity).applicationContext

        friends_recyclerview.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = FriendRequestsListAdapter(requests, context, this@FriendRequestsFragment)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_mainmenu, menu)
    }

    /**
     * Override of onOptionsItemSelected. Navigates to the addFriendsFragment in case the "add"
     * menu-item was clicked
     *
     * @param item: the clicked menu item
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add) {
            findNavController().navigate(R.id.action_friendsMenuFragment_to_addFriendsFragment)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * Override of onStart. Sets the toolbar header text and limits backnavigation on home pressed
     * to one layer
     */
    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).toolbar.findViewById<TextView>(R.id.mainmenu_toolbar_title)
            .setText(R.string.submenu_friends_toolbar_title)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * Override of onResume. Sets the toolbar header text and limits backnavigation on home pressed
     * to one layer
     */
    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).toolbar.findViewById<TextView>(R.id.mainmenu_toolbar_title)
            .setText(R.string.submenu_friends_toolbar_title)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }


    companion object {
        fun newInstance(): FriendRequestsFragment = FriendRequestsFragment()
    }
}