package com.secret.palpatine.ui.mainmenu

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.secret.palpatine.R
import com.secret.palpatine.data.model.friends.friend.FriendsListAdapter
import com.secret.palpatine.data.model.friends.friendgroup.FriendGroup
import com.secret.palpatine.data.model.friends.friendgroup.FriendGroupAdapter
import com.secret.palpatine.data.model.user.User
import kotlinx.android.synthetic.main.activity_main_menu.*
import kotlinx.android.synthetic.main.fragment_friendsmenu.*


/**
 * A simple [Fragment] subclass.
 * Use the [FriendsMenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FriendsMenuFragment : Fragment(), FriendsListAdapter.FriendListAdapterListener {

    /**
     * MainMenuViewModel object for communicating with firebase
     */
    private lateinit var viewModel: MainMenuViewModel


    /**
     * Override of onCreate. Initial set of the MainMenuViewModel.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainMenuViewModel::class.java)
        setHasOptionsMenu(true)
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
        return inflater.inflate(R.layout.fragment_friendsmenu, container, false)
    }

    /**
     * Override of onViewCreated. Observes this user's friendlist and populates the RecyclerView with
     * the result.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loading.visibility = View.VISIBLE

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

        viewModel.acceptFriendRequestResult.observe(viewLifecycleOwner, Observer {
            val loginResult = it ?: return@Observer

            if (loginResult.success) {
                viewModel.refreshUserFriends()
            }
        })

        viewModel.refreshUserFriends()

    }

    /**
     * Shows an error text in case the loading of the friends failed
     */
    private fun showErrorLoading() {

        errorText.text = "Error while loading your friends..."

        errorText.visibility = View.VISIBLE
        friends_recyclerview.visibility = View.GONE
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
            val friendGroup: FriendGroup =
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
        friends_recyclerview.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = FriendGroupAdapter(friendGroupList, context, this@FriendsMenuFragment)
        }
    }

    /**
     * Override of onCreateOptionsMenu. Inflates the menu
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_mainmenu, menu)
    }

    /**
     * Override of onOptionsItemSelected. Navigates the the addFriendsFragment in case the user
     * wants to add a friend
     *
     * @param item: the MenuItem clicked
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

    override fun onSelect(data: User) {
    }

    companion object {
        fun newInstance(): FriendsMenuFragment = FriendsMenuFragment()
    }


}
