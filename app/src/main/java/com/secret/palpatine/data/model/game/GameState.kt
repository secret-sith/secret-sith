package com.secret.palpatine.data.model.game

/**
 * Class describing all possible game states
 *
 * pending --> Game waits until host starts the game
 * started --> Game is currently running
 * finished --> Game is finished
 */
enum class GameState {
    pending,
    started,
    finished}