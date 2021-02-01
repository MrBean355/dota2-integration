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

import com.github.mrbean355.admiralbulldog.game.findMidas
import com.github.mrbean355.admiralbulldog.game.heroItems
import com.github.mrbean355.dota2.GameState

class OnMidasReady : SoundTrigger {

    override fun shouldPlay(previous: GameState, current: GameState): Boolean {
        val currentlyCastable = current.heroItems?.findMidas()?.canCast ?: return false
        val previouslyCastable = previous.heroItems?.findMidas()?.canCast

        return (previouslyCastable == null || !previouslyCastable) && currentlyCastable
    }
}