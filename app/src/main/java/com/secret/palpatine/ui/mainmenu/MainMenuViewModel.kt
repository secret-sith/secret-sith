package com.secret.palpatine.ui.mainmenu

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.toObject
import com.secret.palpatine.data.model.user.User
import com.secret.palpatine.data.model.friends.friend.FriendListResult
import com.secret.palpatine.data.model.friends.friend.FriendRepository
import com.secret.palpatine.data.model.friends.friend.request.FriendRequest
import com.secret.palpatine.data.model.friends.friend.request.FriendRequestCountResult
import com.secret.palpatine.data.model.friends.friend.request.FriendRequestListResult
import com.secret.palpatine.data.model.friends.friend.request.FriendRequestResult
import com.secret.palpatine.data.model.game.CreateGameResult
import com.secret.palpatine.data.model.game.CurrentGameResult
import com.secret.palpatine.data.model.game.Game
import com.secret.palpatine.data.model.game.GameRepository
import com.secret.palpatine.data.model.invitation.Invite
import com.secret.palpatine.data.model.invitation.InviteListResult
import com.secret.palpatine.data.model.invitation.InviteRepository
import com.secret.palpatine.data.model.player.Player
import com.secret.palpatine.data.model.player.PlayersResult
import com.secret.palpatine.data.model.user.UserRepository

/**
 * Created by Florian Fuchs on 08.06.2020.
 */
class MainMenuViewModel constructor(
    val auth: FirebaseAuth,
    private val friendRepository: FriendRepository,
    private val gameRepository: GameRepository,
    private val userRepository: UserRepository,
    private val inviteRepository: InviteRepository
) : ViewModel() {

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


    private val _createGameResult = MutableLiveData<CreateGameResult>()
    val createGameResult: LiveData<CreateGameResult> = _createGameResult

    private val _currentGameResult = MutableLiveData<CurrentGameResult>()
    val currentGameResult: LiveData<CurrentGameResult> = _currentGameResult

    private val _inviteListResult = MutableLiveData<InviteListResult>()
    val inviteListResult: LiveData<InviteListResult> = _inviteListResult

    var userId: String? = null


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

    fun getUserFriendRequestCount() {
        friendRepository.getFriendRequests(auth.currentUser!!).addOnSuccessListener { documents ->
            _friendsRequestCountResult.value = FriendRequestCountResult(success = documents.size())
        }
    }

    fun acceptFriendRequest(id: String) {

        friendRepository.acceptAsFriend(id).addOnSuccessListener {

            _acceptFriendRequestResult.value = FriendRequestResult(success = true)
        }.addOnFailureListener {
            _acceptFriendRequestResult.value = FriendRequestResult(success = false, error = 1)
        }
    }


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

    fun startGame() {
        gameRepository.createGame(
            _usersToStartGame,
            auth.currentUser!!.uid,
            auth.currentUser!!.displayName!!
        ).addOnCompleteListener {
            if (it.exception != null) {
                _createGameResult.value = CreateGameResult(success = false, error = 1)
            } else {
                _createGameResult.value = CreateGameResult(success = true, gameId = it.result)
            }
        }
    }

    fun checkCurrentUsersGame() {

        userRepository.getLiveUser(auth.currentUser!!.uid).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("GAME", "Listen failed.", e)
                _currentGameResult.value = CurrentGameResult(error = 1)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {

                val user = snapshot!!.toObject<User>()
                if (user?.currentGame != null) {
                    userId = auth.currentUser!!.uid
                    _currentGameResult.value = CurrentGameResult(gameId = user.currentGame)
                } else {
                    _currentGameResult.value = CurrentGameResult(error = 1)

                }
            } else {
                _currentGameResult.value = CurrentGameResult(error = 1)
            }

        }
    }


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

    fun acceptInvite(invite: Invite) {
        inviteRepository.acceptInvite(invite)
    }

}