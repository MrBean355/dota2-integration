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

import com.github.mrbean355.dota2.GameState
import kotlin.reflect.KClass

val SOUND_TRIGGER_TYPES: Set<SoundTriggerType> = setOf(
        OnBountyRunesSpawn::class,
        OnKill::class,
        OnDeath::class,
        OnRespawn::class,
        OnHeal::class,
        OnSmoked::class,
        OnMidasReady::class,
        OnMatchStart::class,
        OnVictory::class,
        OnDefeat::class,
        Periodically::class
)

typealias SoundTriggerType = KClass<out SoundTrigger>

interface SoundTrigger {
    /** Examine the states and decide if a should should be played. */
    fun shouldPlay(previous: GameState, current: GameState): Boolean
}
