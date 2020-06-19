package com.secret.palpatine.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.secret.palpatine.data.model.friends.friend.FriendRepository
import com.secret.palpatine.data.model.game.GameRepository
import com.secret.palpatine.data.model.user.UserRepository
import com.secret.palpatine.ui.mainmenu.MainMenuViewModel

/**
 * Created by Florian Fuchs on 19.06.2020.
 */
class GameViewModelFactory(
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GameViewModel(Firebase.auth, GameRepository(), UserRepository()) as T
    }
}