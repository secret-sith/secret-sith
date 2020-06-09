package com.secret.palpatine.ui.mainmenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.secret.palpatine.R
import com.secret.palpatine.data.model.friends.friend.FriendRepository
import kotlinx.android.synthetic.main.activity_main_menu.*


/**
 * Created by Florain Fuchs on 09.06.2020.
 */
class FriendsFragment : Fragment() {
    // When requested, this adapter returns a DemoObjectFragment,
    // representing an object in the collection.
    private lateinit var adapter: FriendsPageAdapter
    private lateinit var viewPager: ViewPager
    private lateinit var viewModel: MainMenuViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(
            this,
            MainMenuViewModelFactory(auth = Firebase.auth, friendRepository = FriendRepository())
        )
            .get(MainMenuViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_friendtabs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = FriendsPageAdapter(childFragmentManager)
        viewPager = view.findViewById(R.id.pager)
        viewPager.adapter = adapter
        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)
        tabLayout.setupWithViewPager(viewPager)

        viewModel.friendsRequestCountResult.observe(viewLifecycleOwner, Observer {
            val result = it ?: return@Observer

            if (result.success != null) {

                val badge: BadgeDrawable = tabLayout.getTabAt(1)!!.orCreateBadge
                badge.isVisible = true
                badge.number = result.success
            }

        })

        viewModel.getUserFriendRequestCount()
    }

    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).toolbar.findViewById<TextView>(R.id.mainmenu_toolbar_title)
            .setText(R.string.submenu_friends_toolbar_title)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).toolbar.findViewById<TextView>(R.id.mainmenu_toolbar_title)
            .setText(R.string.submenu_friends_toolbar_title)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

}

