package com.example.secret.palpatine.ui.mainmenu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.secret.palpatine.R
import com.example.secret.palpatine.data.model.friends.friend.FriendsListAdapter
import com.example.secret.palpatine.data.model.dummy_Friends
import com.example.secret.palpatine.data.model.friends.friend.Friend
import com.example.secret.palpatine.data.model.friends.friendgroup.FriendGroup
import com.example.secret.palpatine.data.model.friends.friendgroup.FriendGroupAdapter
import kotlinx.android.synthetic.main.activity_main_menu.*
import kotlinx.android.synthetic.main.fragment_friendsmenu.*


/**
 * A simple [Fragment] subclass.
 * Use the [FriendsMenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FriendsMenuFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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


        val letterList: MutableList<Char> = ArrayList()
        val friendNameList: MutableList<String> = ArrayList()
        for (friend in dummy_Friends){
            letterList.add(friend.name.first())
            friendNameList.add(friend.name)
        }
        val letterSet: Set<Char> = letterList.toSortedSet()
        val friendGroupList: MutableList<FriendGroup> = ArrayList()
        for (letter in letterSet){
            val friendGroup: FriendGroup = FriendGroup(letter, ArrayList())
            for (name in friendNameList){
                if (name.first() == letter){
                    friendGroup.friendList.add(Friend(name))
                }
            }
            friendGroupList.add(friendGroup)
        }
        val context = (activity as AppCompatActivity).applicationContext
        friends_recyclerview.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = FriendGroupAdapter(friendGroupList,context)
        }
    }


    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).toolbar.findViewById<TextView>(R.id.mainmenu_toolbar_title).setText(R.string.submenu_friends_toolbar_title)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).toolbar.findViewById<TextView>(R.id.mainmenu_toolbar_title).setText(R.string.submenu_friends_toolbar_title)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }


    companion object {
        fun newInstance(): FriendsMenuFragment = FriendsMenuFragment()
    }
}
