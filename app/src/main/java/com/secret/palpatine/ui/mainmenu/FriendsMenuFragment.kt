package com.secret.palpatine.ui.mainmenu

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

import com.secret.palpatine.R
import com.secret.palpatine.data.model.User
import com.secret.palpatine.data.model.friends.friend.FriendRepository
import com.secret.palpatine.data.model.friends.friendgroup.FriendGroup
import com.secret.palpatine.data.model.friends.friendgroup.FriendGroupAdapter
import com.secret.palpatine.databinding.FragmentFriendsmenuBinding
import kotlinx.android.synthetic.main.activity_main_menu.*
import kotlinx.android.synthetic.main.fragment_friendsmenu.*
import java.security.KeyStore


/**
 * A simple [Fragment] subclass.
 * Use the [FriendsMenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FriendsMenuFragment : Fragment() {

    private lateinit var viewModel: MainMenuViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(
            this,
            MainMenuViewModelFactory(auth = Firebase.auth, friendRepository = FriendRepository())
        )
            .get(MainMenuViewModel::class.java)
        setHasOptionsMenu(true)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friendsmenu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loading.visibility = View.VISIBLE

        viewModel.friendListResult.observe(viewLifecycleOwner, Observer {
            val loginResult = it ?: return@Observer

            if (loginResult.error != null) {
                loading.visibility = View.GONE
                showErrorLoading(loginResult.error)
            }
            if (loginResult.success != null) {
                loading.visibility = View.GONE
                generateLetteredList(loginResult.success)
            }
        })

        viewModel.getUserFriends()

    }

    private fun showErrorLoading(error: Int) {

        errorText.text = "Error while loading your friends..."

        errorText.visibility = View.VISIBLE
        friends_recyclerview.visibility = View.GONE
    }

    private fun generateLetteredList(friendList: List<User>) {
        val letterList: MutableList<Char> = ArrayList()
        val userList: MutableList<User> = ArrayList()
        for (user in friendList) {
            letterList.add(user.username.first())
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
                if (user.username.first() == letter) {
                    friendGroup.friendList.add(user)
                }
            }
            friendGroupList.add(friendGroup)
        }
        val context = (activity as AppCompatActivity).applicationContext
        friends_recyclerview.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = FriendGroupAdapter(friendGroupList, context)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_mainmenu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add){
            findNavController().navigate(R.id.action_friendsMenuFragment_to_addFriendsFragment)
            return true
        }

        return super.onOptionsItemSelected(item)
    }
    

    companion object {
        fun newInstance(): FriendsMenuFragment = FriendsMenuFragment()
    }
}
