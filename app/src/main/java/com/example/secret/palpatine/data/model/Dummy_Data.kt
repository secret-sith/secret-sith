package com.example.secret.palpatine.data.model

import com.example.secret.palpatine.data.model.friends.friend.Friend
import com.example.secret.palpatine.data.model.invitation.Invite
import java.time.LocalDateTime
import java.time.Month
import java.util.*

public val dummy_Invites = listOf(
    Invite("Alexander Rose", LocalDateTime.of(2020,Month.MAY,26,21,1),"Grüß Gott! Nach langer Zeit mal wieder eine Runde?"),
    Invite("Winston Churchill",LocalDateTime.of(1941, Month.MAY, 26, 14, 31),"Hello dear Sir! Do you want to catch Hitler with me?"),
    Invite( "Franklin D. Roosevelt",LocalDateTime.of(1944, Month.MAY, 11, 21, 22),"Im planing a trip to france. You want to come?"),
    Invite("Joseph Stalin",LocalDateTime.of(1945, Month.APRIL, 30, 3, 17),"I need someone to check the basement. Do you have time?"),
    Invite("Future Man", LocalDateTime.of(2020, Month.MAY,26,21,1),"Wahnsinn Jungs. Euer Game ging durch die Decke!!")
)

public val dummy_Friends = listOf(
    Friend("Alexander Rose"),
    Friend("Adrian Uffmann"),
    Friend("Florian Fuchs"),
    Friend("Steffen Illium"),
    Friend("Tobias Weber"),
    Friend("Joseph Stalin"),
    Friend("Winston Churchill"),
    Friend("Jango Fett"),
    Friend("Anakin Skywalker")

)

public val dummy_game = Game(1)

public val dummy_users = listOf(
    User("Alex","05-06-2020", "00000001", UUID.randomUUID(), dummy_game),
    User("Adrian","05-06-2020","00000010",UUID.randomUUID(), dummy_game),
    User("Florian","05-06-2020","00000011", UUID.randomUUID(), dummy_game),
    User("Tobias","05-06-2020","00000011", UUID.randomUUID(), dummy_game),
    User("Steffen","05-06-2020","00000011", UUID.randomUUID(), dummy_game)
)

public val dummy_players = listOf(
    Player(PlayerRole.SECRET_PALPATINE, dummy_users[0], dummy_game),
    Player(PlayerRole.LOYALIST, dummy_users[1], dummy_game),
    Player(PlayerRole.LOYALIST, dummy_users[2], dummy_game),
    Player(PlayerRole.IMPERIALIST, dummy_users[3], dummy_game),
    Player(PlayerRole.IMPERIALIST, dummy_users[4], dummy_game)
)