package com.secret.palpatine.ui.mainmenu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.secret.palpatine.R
import com.secret.palpatine.data.model.friends.friend.FriendsListAdapter
import com.secret.palpatine.data.model.user.User
import kotlinx.android.synthetic.main.activity_main_menu.*
import kotlinx.android.synthetic.main.fragment_add_friends.*
import kotlinx.android.synthetic.main.fragment_add_friends.progress_overlay

class FriendsAddFragment : Fragment(), FriendsListAdapter.FriendListAdapterListener,
    SearchView.OnQueryTextListener {

    private lateinit var searchListAdapter: FriendsListAdapter
    private lateinit var viewModel: MainMenuViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainMenuActivity.isInSelectionMode = true

        viewModel = ViewModelProvider(this).get(MainMenuViewModel::class.java)
        retainInstance = true
        searchListAdapter = FriendsListAdapter(listOf(), this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_friends, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        friends_recyclerview.apply {
            layoutManager = GridLayoutManager(activity, 2)
            adapter = searchListAdapter
        }

        search.setOnQueryTextListener(this)

        viewModel.friendsSearchResult.observe(viewLifecycleOwner, Observer {
            Log.d("Players", it.toString())
            if (it.success != null) {
                populateList(it.success)
            }
            if (it.error != null) {
                Toast.makeText(context, "Error while search for new people...", Toast.LENGTH_SHORT)
                    .show()
            }
            progress_overlay.visibility = View.GONE
        })

        btn_add_friends.setOnClickListener {
            progress_overlay.visibility = View.VISIBLE

            viewModel.sendFriendRequests().addOnSuccessListener {
                Toast.makeText(context, "Send friendship requests!", Toast.LENGTH_SHORT).show()
                progress_overlay.visibility = View.GONE
                findNavController().popBackStack(R.id.friendsMenuFragment, false)
            }.addOnFailureListener {
                Toast.makeText(context, "Could not send requests!", Toast.LENGTH_SHORT).show()
                progress_overlay.visibility = View.GONE

            }
        }

    }

    private fun populateList(users: List<User>?) {

        if (users != null) {
            if (users.isEmpty()) {
                txt_empty.visibility = View.VISIBLE
                btn_add_friends.visibility = View.GONE
                txt_empty.text = "No users found ..."
                searchListAdapter.setItems(listOf())
                searchListAdapter.notifyDataSetChanged()

            } else {
                btn_add_friends.visibility = View.VISIBLE
                txt_empty.visibility = View.GONE

                searchListAdapter.setItems(users)
                searchListAdapter.notifyDataSetChanged()
            }

        }
    }

    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).toolbar.findViewById<TextView>(R.id.mainmenu_toolbar_title)
            .setText(
                R.string.submenu_friends_toolbar_title
            )
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).toolbar.findViewById<TextView>(R.id.mainmenu_toolbar_title)
            .setText(
                R.string.submenu_friends_toolbar_title
            )
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    companion object {
        fun newInstance(): FriendsAddFragment = FriendsAddFragment()
    }

    override fun onDestroyView() {
        MainMenuActivity.isInSelectionMode = false
        super.onDestroyView()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {

        viewModel.searchForFriends(query);
        progress_overlay.visibility = View.VISIBLE
        txt_empty.visibility = View.GONE
        btn_add_friends.visibility = View.GONE
        searchListAdapter.setItems(listOf())
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }

    override fun onSelect(data: User) {
        viewModel.updateFriendsToAddList(data)
        btn_add_friends.isEnabled = true
    }
}