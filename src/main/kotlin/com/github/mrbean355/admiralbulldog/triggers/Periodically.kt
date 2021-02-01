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
import kotlin.random.Random

class Periodically : SoundTrigger {
    private var nextPlayClockTime = UNINITIALISED

    override fun shouldPlay(previous: GameState, current: GameState): Boolean {
        val currentTime = current.map?.clockTime ?: return false

        if (nextPlayClockTime == UNINITIALISED) {
            nextPlayClockTime = currentTime + randomiseDelay()
        } else if (currentTime >= nextPlayClockTime) {
            nextPlayClockTime += randomiseDelay()
            return true
        }
        return false
    }

    private fun randomiseDelay(): Int {
        // Cast to double so we can randomise a non-whole amount of minutes.
        val minQuietTime = ConfigPersistence.getMinPeriod().toDouble()
        val maxQuietTime = ConfigPersistence.getMaxPeriod().toDouble()
        return if (minQuietTime != maxQuietTime) {
            Random.nextDouble(minQuietTime, maxQuietTime)
        } else {
            minQuietTime
        }.toInt() * 60
    }
}