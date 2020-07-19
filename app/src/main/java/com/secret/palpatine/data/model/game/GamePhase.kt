package com.secret.palpatine.data.model.game

/**
 * Depicts the current phase of the game.
 *
 * nominate_chancellor --> Presidential candidate has to nominate a potential chancellor
 * vote --> Players vote for the current government suggestion
 * president_discard_policy --> President needs to discard a policy
 * chancellor_discard_policy --> Chancellor needs to discard a policy
 * policy_peek --> Special Power: Current president can have a peek at the cards for the next round
 * kill --> Special Power: President is able to kill a player.
 * president_accept_veto --> Special Power: President chooses whether to accept the veto suggestion of the chancellor or not
 * killed --> Displayed to players that were killed, which marks their inability to participate in other phases.
 */
enum class GamePhase {
    nominate_chancellor,
    vote,
    president_discard_policy,
    chancellor_discard_policy,
    policy_peek,
    kill,
    president_accept_veto,
    killed
}


