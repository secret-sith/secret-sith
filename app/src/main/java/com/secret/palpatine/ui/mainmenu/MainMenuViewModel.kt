package com.secret.palpatine.ui.mainmenu

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.secret.palpatine.data.model.friends.friend.FriendListResult
import com.secret.palpatine.data.model.friends.friend.FriendRepository
import com.secret.palpatine.data.model.friends.friend.FriendShipState
import com.secret.palpatine.data.model.friends.friend.request.FriendRequest
import com.secret.palpatine.data.model.friends.friend.request.FriendRequestCountResult
import com.secret.palpatine.data.model.friends.friend.request.FriendRequestListResult
import com.secret.palpatine.data.model.friends.friend.request.FriendRequestResult
import com.secret.palpatine.data.model.game.GameRepository
import com.secret.palpatine.data.model.invitation.Invite
import com.secret.palpatine.data.model.invitation.InviteListResult
import com.secret.palpatine.data.model.invitation.InviteRepository
import com.secret.palpatine.data.model.user.User
import com.secret.palpatine.data.model.user.UserRepository
import com.secret.palpatine.util.USERS

/**
 * Created by Florian Fuchs on 08.06.2020.
 */
class MainMenuViewModel : ViewModel() {
    private val auth = Firebase.auth
    private val friendRepository = FriendRepository()
    private val gameRepository = GameRepository()
    private val userRepository = UserRepository()
    private val inviteRepository = InviteRepository()

    private val _friendsListResult = MutableLiveData<FriendListResult>()
    val friendListResult: LiveData<FriendListResult> = _friendsListResult

    private val _friendsRequestResult = MutableLiveData<FriendRequestListResult>()
    val friendsRequestResult: LiveData<FriendRequestListResult> = _friendsRequestResult

    private val _friendsRequestCountResult = MutableLiveData<FriendRequestCountResult>()
    val friendsRequestCountResult: LiveData<FriendRequestCountResult> = _friendsRequestCountResult


    private val _acceptFriendRequestResult = MutableLiveData<FriendRequestResult>()
    val acceptFriendRequestResult: LiveData<FriendRequestResult> = _acceptFriendRequestResult

    private val _usersToStartGame = mutableListOf<User>()
    val usersToStartGame = MutableLiveData<List<User>>()

    private val _friendsToAdd = mutableListOf<User>()

    private val _inviteListResult = MutableLiveData<InviteListResult>()
    val inviteListResult: LiveData<InviteListResult> = _inviteListResult

    var userId: String? = auth.currentUser!!.uid

    private val _friendsSearchResult = MutableLiveData<FriendListResult>()
    val friendsSearchResult: LiveData<FriendListResult> = _friendsSearchResult

    /**
     * Function to update the friendList object in case there are changes
     */
    fun refreshUserFriends() {
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

    /**
     * Function to get the amount of pending friend requests for this user.
     */
    fun getUserFriendRequestCount() {
        friendRepository.getFriendRequests(auth.currentUser!!).addOnSuccessListener { documents ->
            _friendsRequestCountResult.value = FriendRequestCountResult(success = documents.size())
        }
    }

    /**
     * Function to accept a friend request of a user
     *
     * @param id: the UUID of the firebase document of the user
     */
    fun acceptFriendRequest(id: String) {

        friendRepository.acceptAsFriend(id).addOnSuccessListener {

            _acceptFriendRequestResult.value = FriendRequestResult(success = true)
        }.addOnFailureListener {
            _acceptFriendRequestResult.value = FriendRequestResult(success = false, error = 1)
        }
    }

    /**
     * Function to search this users friendlist for matches to a query.
     *
     * @param userNameQuery: query text to find suggestions to
     */
    fun searchForFriends(userNameQuery: String?) {

        if (userNameQuery.isNullOrBlank()) {
            return
        }

        friendRepository.searchForFriends(userNameQuery).addOnSuccessListener { documents ->
            var friendList: MutableList<User> = mutableListOf()

            for (document in documents) {
                var user = document.toObject(User::class.java)
                friendList.add(user)
            }

            Log.d("friends", friendList.toString())

            friendList =
                friendList.filter { user ->
                    !user.friends.contains(
                        Firebase.firestore.collection("users").document(auth.currentUser!!.uid)
                    ) && user.id != auth.currentUser!!.uid
                }.toMutableList()

            _friendsSearchResult.value = FriendListResult(success = friendList)
        }.addOnFailureListener {
            Log.e("search", it.message)
            _friendsSearchResult.value = FriendListResult(error = 1)

        }
    }


    /**
     * Adds a user to the list of players to start a game with
     * @param user: the User to be added
     */
    fun updateUserToStartGameList(user: User) {
        Log.d("User to add", user.toString())
        if (user.isSelected) {
            _usersToStartGame.add(user)

        } else {
            _usersToStartGame.remove(user)
        }
        Log.d("Game List Size", _usersToStartGame.size.toString())
        usersToStartGame.value = _usersToStartGame
    }

    /**
     * Function to start the game
     */
    fun startGame(): Task<String> {
        return gameRepository.createGame(
            _usersToStartGame,
            auth.currentUser!!.uid,
            auth.currentUser!!.displayName!!
        )
    }

    /**
     * Updates the selection list in case of a change
     */
    fun updateFriendsToAddList(user: User) {
        if (user.isSelected) {
            _friendsToAdd.add(user)

        } else {
            _friendsToAdd.remove(user)
        }
    }

    /**
     * Function to send a friend request to each user in the friends-to-add list
     */
    fun sendFriendRequests(): Task<Void> {

        val db = Firebase.firestore

        var plist: ArrayList<Task<DocumentReference>> = arrayListOf()

        for (user in _friendsToAdd) {
            val friendToAdd = hashMapOf(
                "user" to db.collection(USERS).document(auth.currentUser!!.uid),
                "friend" to db.collection(USERS).document(user.id),
                "state" to FriendShipState.requested
            )
            plist.add(
                db.collection("friendships").add(friendToAdd)
            )
        }


        return Tasks.whenAll(plist)
    }

    /**
     * Function to get the pending friend requests for this user sent by other users
     */
    fun getUserFriendRequests() {

        friendRepository.getFriendRequests(auth.currentUser!!).addOnSuccessListener { documents ->


            var userList: ArrayList<Task<DocumentSnapshot>> = arrayListOf()
            var friendList: MutableMap<DocumentReference, FriendRequest> = mutableMapOf()

            for (document in documents) {
                val userRef = document.getDocumentReference("user")
                userList.add(friendRepository.getUserByReference(userRef!!))
                friendList.set(userRef!!, FriendRequest(id = document.id, user = null))
            }

            Tasks.whenAllSuccess<DocumentSnapshot>(userList).addOnSuccessListener {

                for (document in it) {

                    var user = document.toObject(User::class.java)
                    var mapEntry = friendList[document.reference]
                    mapEntry?.user = user
                }
                _friendsRequestResult.value =
                    FriendRequestListResult(success = friendList.values.toList())

            }

        }.addOnFailureListener {
            _friendsRequestResult.value = FriendRequestListResult(error = 1)

        }

    }

    /**
     * Function to get the pending invites for this user by other users
     */
    fun getInvites() {

        inviteRepository.getInvites(auth.currentUser!!.uid).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("INVITE", "Listen failed.", e)
                return@addSnapshotListener
            }
            if (snapshot != null && !snapshot.isEmpty()) {
                var inviteList: MutableList<Invite> = mutableListOf()
                for (document in snapshot!!.documents) {
                    inviteList.add(document.toObject<Invite>()!!)
                }
                _inviteListResult.value = InviteListResult(success = inviteList)
            } else {
                _inviteListResult.value = InviteListResult(error = 1)
            }
        }
    }

    /**
     * Function to accept an invite
     *
     * @param invite: the invite to accept
     */
    fun acceptInvite(invite: Invite): Task<Void> {
        return inviteRepository.acceptInvite(invite)
    }

}