package com.secret.palpatine.ui.mainmenu

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

/**
 * Created by Florian Fuchs on 09.06.2020.
 */
class FriendsPageAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getCount(): Int = 2

    /**
     * Override of getItem. Returns a FriendsMenuFragment in case the parameter is zero, else a
     * FriendRequestsFragment
     *
     * @param i: integer indicating what to return
     * @return the correct fragment
     */
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

    /**
     * Override of getPageTitle. Returns a string based on the position provided
     *
     * @param position: the position determining the return string
     * @return a string used as a header.
     */
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
