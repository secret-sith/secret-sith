package com.secret.palpatine.ui.mainmenu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager

import com.secret.palpatine.R
import com.secret.palpatine.data.model.user.User
import com.secret.palpatine.data.model.friends.friend.FriendsListAdapter
import com.secret.palpatine.data.model.friends.friendgroup.FriendGroup
import com.secret.palpatine.data.model.friends.friendgroup.FriendGroupAdapter
import kotlinx.android.synthetic.main.activity_main_menu.*
import kotlinx.android.synthetic.main.fragment_start_game_menu.*

/**
 * Fragment that is being pushed when a user presses "Start Game" in the MainMenu
 *
 * Provides an interface for selecting the friends you wish to start a game with
 */
class StartGameMenuFragment : Fragment(), FriendsListAdapter.FriendListAdapterListener,
    View.OnClickListener {

    /**
     * ViewModel for the MainMenu
     */
    private lateinit var viewModel: MainMenuViewModel

    /**
     * List holding the selected players
     */
    private var selectedUsers: List<User> = listOf()

    /**
     * Override of onCreate. Initial set of the ViewModel
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        viewModel = ViewModelProvider(this).get(MainMenuViewModel::class.java)
    }

    /**
     * Override of onCreateView. Sets the according layout
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_start_game_menu, container, false)
    }

    /**
     * Override of onViewCreated
     *
     * Attaches an observer on the logged-in user's friendlist. Populates the selection-view on success.
     * Attaches a click-listener on the startbutton
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loading.visibility = View.VISIBLE
        start_game_button.isClickable = true
        start_game_button.setOnClickListener(this)
        viewModel.friendListResult.observe(viewLifecycleOwner, Observer {
            val loginResult = it ?: return@Observer

            if (loginResult.error != null) {
                loading.visibility = View.GONE
                showErrorLoading()
            }
            if (loginResult.success != null) {
                loading.visibility = View.GONE
                generateLetteredList(loginResult.success)
            }
        })
        viewModel.usersToStartGame.observe(viewLifecycleOwner, Observer {
            val userList = it ?: return@Observer
            selectedUsers = userList
            // start_game_button.isClickable = selectedUsers.size > 3

        })

        viewModel.refreshUserFriends()
    }

    /**
     * Shows an error message on the screen in case something goes wrong
     */
    private fun showErrorLoading() {

        errorText.text = "Error while loading your friends..."

        errorText.visibility = View.VISIBLE
        start_game_recyclerview.visibility = View.GONE
    }

    /**
     * Takes a list of users and creates according FriendGroup objects that can be consumed
     * by the recyclerview. Applies the adapter and layoutmanager to the recyclerview
     *
     * @param friendList: unsorted list of User objects. to be turned in FriendGroup objects.
     */
    private fun generateLetteredList(friendList: List<User>) {
        val letterList: MutableList<Char> = ArrayList()
        val userList: MutableList<User> = ArrayList()
        for (user in friendList) {
            letterList.add(user.username.decapitalize().first())
            userList.add(user)
        }
        val letterSet: Set<Char> = letterList.toSortedSet()
        val friendGroupList: MutableList<FriendGroup> = ArrayList()
        for (letter in letterSet) {
            val friendGroup =
                FriendGroup(
                    letter,
                    ArrayList()
                )
            for (user in userList) {
                if (user.username.decapitalize().first() == letter) {
                    friendGroup.friendList.add(user)
                }
            }
            friendGroupList.add(friendGroup)
        }
        val context = (activity as AppCompatActivity).applicationContext
        start_game_recyclerview.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = FriendGroupAdapter(friendGroupList, context, this@StartGameMenuFragment)
        }
    }

    /**
     * Override of onClick. Binds a function to start the game on the "Start Game" button
     * @param v: the view that got clicked
     */
    override fun onClick(v: View) {
        when (v.id) {
            R.id.start_game_button -> {
                viewModel.startGame().addOnSuccessListener {
                    val bundle = bundleOf(Pair("gameId", it))
                    findNavController().navigate(R.id.action_startGameMenuFragment_to_gamePendingFragment,bundle)

                }
            }
        }
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



    override fun onDestroyView() {
        viewModel.resetUsersToStartGame()
        super.onDestroyView()
    }

    /**
     * Override of onSelect. Updates the user list in the viewmodel in case of a selection change
     */
    override fun onSelect(data: User) {
        viewModel.updateUserToStartGameList(data)
    }

    companion object {
        fun newInstance(): StartGameMenuFragment =
            StartGameMenuFragment()
    }
}
