package com.tiki.diceelysium.model

import androidx.compose.ui.graphics.Color
import kotlin.random.Random

data class Dice(
    var type: DiceType,
    var value: Int,
    var diceColor: Color
) {
    enum class DiceType(val min: Int, val max: Int) {
        D6(1, 6)
    }

    fun roll() {
        value = Random.nextInt(this.type.min, this.type.max + 1)
    }
}
