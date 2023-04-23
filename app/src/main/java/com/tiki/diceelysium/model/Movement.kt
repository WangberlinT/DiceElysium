package com.tiki.diceelysium.model

data class Movement(
    val offsetYPercent: Float = 0f,
    val applyBlur: Boolean = false,
    val durationMS: Int = 1000
)
