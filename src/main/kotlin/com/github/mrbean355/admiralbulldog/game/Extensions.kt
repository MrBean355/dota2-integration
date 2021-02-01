package com.github.mrbean355.admiralbulldog.game

import com.github.mrbean355.dota2.*

inline val GameState.player: Player?
    get() = players?.values?.singleOrNull()

inline val GameState.hero: Hero?
    get() = heroes?.values?.singleOrNull()

inline val GameState.heroItems: HeroItems?
    get() = items?.values?.singleOrNull()

fun GameState.hasValidProperties(): Boolean {
    return null !in listOf(map, player, hero, heroItems)
}

@Suppress("NOTHING_TO_INLINE")
inline fun HeroItems.findMidas(): Item? {
    return inventory.find { it.name == "item_hand_of_midas" }
}
