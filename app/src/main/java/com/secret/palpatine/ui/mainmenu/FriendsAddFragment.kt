package com.secret.palpatine.ui.mainmenu

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.secret.palpatine.R
import com.secret.palpatine.data.model.user.User
import com.secret.palpatine.data.model.dummy_users
import com.secret.palpatine.data.model.friends.friend.FriendsListAdapter
import kotlinx.android.synthetic.main.activity_main_menu.*
import kotlinx.android.synthetic.main.fragment_friendsmenu.*

class FriendsAddFragment: Fragment(),FriendsListAdapter.FriendListAdapterListener, SearchView.OnQueryTextListener {

    private lateinit var adapter: FriendsListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        adapter = FriendsListAdapter(dummy_users, this)
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
            layoutManager=LinearLayoutManager(activity)
            adapter=adapter
        }
    }

    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).toolbar.findViewById<TextView>(R.id.mainmenu_toolbar_title).setText(
            R.string.submenu_friends_toolbar_title)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).toolbar.findViewById<TextView>(R.id.mainmenu_toolbar_title).setText(
            R.string.submenu_friends_toolbar_title)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    companion object {
        fun newInstance(): FriendsAddFragment = FriendsAddFragment()
    }


    override fun onQueryTextSubmit(query: String?): Boolean {

        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }

    override fun onSelect(data: User) {
        TODO("Not yet implemented")
    }
}