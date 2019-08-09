package com.github.mrbean355.admiralbulldog.bytes

import com.github.mrbean355.admiralbulldog.game.GameState

/** Plays a sound just before the clock hits 0. */
class OnMatchStart : SoundByte {
    private var played = false

    override fun shouldPlay(previous: GameState, current: GameState): Boolean {
        if (!played && current.map!!.clock_time >= -5) {
            played = true
            return true
        }
        return false
    }
}