package com.tiki.diceelysium.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tiki.diceelysium.model.Dice
import com.tiki.diceelysium.model.Movement
import com.tiki.diceelysium.ui.theme.Active
import com.tiki.diceelysium.ui.theme.InActive
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class DiceViewModel : ViewModel(){

    private val _diceUIState = MutableStateFlow(DiceUiState())
    val diceUIState = _diceUIState.asStateFlow()


    fun rollDices() = viewModelScope.launch {
        val rollingDurationMs = 2000L
        val confirmDurationMs = 1000L
        _diceUIState.update { currentState ->
            currentState.copy(
                backgroundMovement = Movement(
                    offsetYPercent = 25f+Random.nextFloat(),
                    applyBlur = true,
                    durationMS = rollingDurationMs.toInt()
                ),
                isRolling = true
            )
        }
        delay(rollingDurationMs)
        _diceUIState.update { currentState ->
            currentState.copy(
                visible = true,
                isRolling = false,

            ).apply {
                this.dices.forEach {
                    it.diceColor = Active
                    it.roll()
                }
            }
        }
        delay(confirmDurationMs)
        _diceUIState.update { currentState ->
            currentState.copy(visible = false).apply {
                this.dices.forEach {
                    it.diceColor = InActive
                }
            }
        }
        toNextFilm()
    }

    fun addDice() {
        _diceUIState.update { currentState ->
            currentState.copy(
                dices = currentState.dices.toMutableList().apply { add(this.last().copy()) }
            )
        }
    }

    fun reduceDice() {
        _diceUIState.update { currentState ->
            currentState.copy(
                dices = currentState.dices.toMutableList().apply { this.removeLast() }
            )
        }
    }

    private fun toNextFilm() {
        val nextFilmDurationMs = 500L
        val nextFilmOffset = 0.5f
        _diceUIState.update { currentState ->
            currentState.copy(
                backgroundMovement = Movement(
                    offsetYPercent = nextFilmOffset,
                    durationMS = nextFilmDurationMs.toInt()
                )
            )
        }
    }
}

data class DiceUiState(
    val backgroundMovement: Movement = Movement(),
    val visible: Boolean = false,
    val isRolling: Boolean = false,
    val dices: List<Dice> = listOf(
        Dice(Dice.DiceType.D6, 1, InActive)
    )
)

