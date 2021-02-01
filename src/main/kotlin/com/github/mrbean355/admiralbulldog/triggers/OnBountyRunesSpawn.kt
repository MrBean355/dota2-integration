/*
 * Copyright 2021 Michael Johnston
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mrbean355.admiralbulldog.triggers

import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.dota2.GameState
import com.github.mrbean355.dota2.MatchState
import kotlin.math.ceil

/** How often (in seconds) the runes spawn. */
private const val HOW_OFTEN = 5 * 60L

/** Plays a sound shortly before the bounty runes spawn. */
class OnBountyRunesSpawn : SoundTrigger {
    /** Play a sound this many seconds before the bounty runes spawn. */
    private val warningPeriod = ConfigPersistence.getBountyRuneTimer().toLong()
    private var nextPlayTime = UNINITIALISED

    override fun shouldPlay(previous: GameState, current: GameState): Boolean {
        val currentMap = current.map ?: return false
        val matchState = currentMap.matchState
        if (matchState != MatchState.PRE_GAME && matchState != MatchState.GAME_IN_PROGRESS) {
            // Don't play during the picking phase.
            return false
        }
        val currentTime = currentMap.clockTime
        if (nextPlayTime == UNINITIALISED) {
            nextPlayTime = findNextPlayTime(currentTime)
        }
        if (currentTime >= nextPlayTime) {
            val diff = currentTime - nextPlayTime
            nextPlayTime = findNextPlayTime(currentTime + 10)
            if (diff <= warningPeriod) {
                return true
            }
        }
        return false
    }

    private fun findNextPlayTime(clockTime: Long): Long {
        val iteration = ceil((clockTime + warningPeriod) / HOW_OFTEN.toFloat()).toInt()
        val nextPlayTime = iteration * HOW_OFTEN - warningPeriod
        return nextPlayTime.coerceAtLeast(-warningPeriod)
    }
}