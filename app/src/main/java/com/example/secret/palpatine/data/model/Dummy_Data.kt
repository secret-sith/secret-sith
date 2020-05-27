package com.example.secret.palpatine.data.model

import com.example.secret.palpatine.data.model.friends.friend.Friend
import com.example.secret.palpatine.data.model.invitation.Invite
import java.time.LocalDateTime
import java.time.Month

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