package com.secret.palpatine.ui.mainmenu

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.tabs.TabLayout
import com.secret.palpatine.R
import kotlinx.android.synthetic.main.activity_main_menu.*


/**
 * Created by Florian Fuchs on 09.06.2020.
 */
class FriendsFragment : Fragment() {

    /**
     * FriendsPageAdapter object. When requested, this adapter returns a DemoObjectFragment
     * representing an object in the collection
     */
    private lateinit var adapter: FriendsPageAdapter

    /**
     * ViewPager object for switching between the friendslist and the friend requests
     */
    private lateinit var viewPager: ViewPager

    /**
     * MainMenuViewModel object for firebaes operations
     */
    private lateinit var viewModel: MainMenuViewModel

    /**
     * Override of onCreate. Initial set of the MainMenuViewModel
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainMenuViewModel::class.java)
    }

    /**
     * Override of onCreateView. Inflates the correct layout for this fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_friendtabs, container, false)
    }

    /**
     * Override of onViewCreated. Observes the open friend requests for this user and provides an
     * interface to accept and manage the requests if there are some
     */
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
                badge.isVisible = result.success > 0
                badge.number = result.success
            }

        })
        viewModel.acceptFriendRequestResult.observe(viewLifecycleOwner, Observer {
            val loginResult = it ?: return@Observer

            if (loginResult.success) {
                viewModel.getUserFriendRequestCount()
            }
        })
        viewModel.getUserFriendRequestCount()
    }

    /**
     * Override of onCreateOptionsMenu.
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_mainmenu, menu)
    }

    /**
     * Override of onOptionsItemSelected. Navigates the addFriendsFragment in case the item was the
     * add button.
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

}

