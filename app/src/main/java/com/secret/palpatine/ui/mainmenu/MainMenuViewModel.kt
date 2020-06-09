package com.secret.palpatine.ui.mainmenu

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.secret.palpatine.data.model.User
import com.secret.palpatine.data.model.friends.friend.FriendListResult
import com.secret.palpatine.data.model.friends.friend.FriendRepository
import com.secret.palpatine.data.model.friends.friend.request.FriendRequestCountResult

/**
 * Created by Florain Fuchs on 08.06.2020.
 */
class MainMenuViewModel constructor(
    private val auth: FirebaseAuth,
    private val friendRepository: FriendRepository
) : ViewModel() {

    private val _friendsListResult = MutableLiveData<FriendListResult>()
    val friendListResult: LiveData<FriendListResult> = _friendsListResult

    private val _friendsRequestResult = MutableLiveData<FriendListResult>()
    val friendsRequestResult: LiveData<FriendListResult> = _friendsRequestResult

    private val _friendsRequestCountResult = MutableLiveData<FriendRequestCountResult>()
    val friendsRequestCountResult: LiveData<FriendRequestCountResult> = _friendsRequestCountResult

    fun getUserFriends() {
        friendRepository.getUserFriends(auth.currentUser!!).addOnSuccessListener { documents ->
            var friendList: MutableList<User> = mutableListOf()

            for (document in documents) {
                var user = document.toObject(User::class.java)
                friendList.add(user)
            }
            _friendsListResult.value = FriendListResult(success = friendList)
        }.addOnFailureListener {
            _friendsListResult.value = FriendListResult(error = 1)

        }
    }

    fun getUserFriendRequestCount() {
        friendRepository.getFriendRequests(auth.currentUser!!).addOnSuccessListener { documents ->
            _friendsRequestCountResult.value = FriendRequestCountResult(success = documents.size())
        }
    }

    fun getUserFriendRequests() {

        friendRepository.getFriendRequests(auth.currentUser!!).addOnSuccessListener { documents ->


            var userList: ArrayList<Task<DocumentSnapshot>> = arrayListOf()
            for (document in documents) {
                userList.add(friendRepository.getUserByReference(document.getDocumentReference("user")!!))
            }

            Tasks.whenAllSuccess<DocumentSnapshot>(userList).addOnSuccessListener {
                var friendList: MutableList<User> = mutableListOf()

                for (document in it) {
                    var user = document.toObject(User::class.java)
                    friendList.add(user!!)
                }
                _friendsRequestResult.value = FriendListResult(success = friendList)

            }

        }.addOnFailureListener {
            _friendsRequestResult.value = FriendListResult(error = 1)

        }

    }
}