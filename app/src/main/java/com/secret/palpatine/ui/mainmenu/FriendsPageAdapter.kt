package com.secret.palpatine.ui.mainmenu

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

/**
 * Created by Florian Fuchs on 09.06.2020.
 */
class FriendsPageAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getCount(): Int = 2

    override fun getItem(i: Int): Fragment {

        return when (i) {
            0 -> {
                FriendsMenuFragment.newInstance()
            }
            1 -> {
                FriendRequestsFragment.newInstance()
            }
            else -> null!!
        }
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> {
                "All"
            }
            1 -> {
                "Requests"
            }
            else -> ""
        }
    }
}
