package com.secret.palpatine.util

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commit()
}

fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int) {
    supportFragmentManager.inTransaction { add(frameId, fragment) }
}


fun AppCompatActivity.pushFragment(fragment: Fragment, frameId: Int) {
    supportFragmentManager.inTransaction { replace(frameId, fragment) }
}

fun AppCompatActivity.removeFragment(frameId: Int) {
    val fragment = supportFragmentManager.findFragmentById(frameId)
    if (fragment != null) {
        supportFragmentManager.inTransaction { remove(fragment) }
    }
}
