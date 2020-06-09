package com.secret.palpatine.ui.mainmenu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.secret.palpatine.data.model.friends.friend.FriendRepository

/**
 * Created by Florain Fuchs on 08.06.2020.
 */
class MainMenuViewModelFactory(
    private val auth: FirebaseAuth,
    private val friendRepository: FriendRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainMenuViewModel(auth, friendRepository) as T
    }
}