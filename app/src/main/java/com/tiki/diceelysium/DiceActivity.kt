package com.tiki.diceelysium

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tiki.diceelysium.model.Dice
import com.tiki.diceelysium.ui.theme.DiceElysiumTheme
import com.tiki.diceelysium.ui.widgets.D6Dice
import com.tiki.diceelysium.ui.widgets.ScrollableBackground
import com.tiki.diceelysium.ui.widgets.SuccessEffect
import com.tiki.diceelysium.viewmodel.DiceViewModel
import kotlin.math.ceil
import kotlin.math.sqrt

class DiceActivity : ComponentActivity() {
    private val viewModel: DiceViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DiceElysiumTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DiceScreen(viewModel)
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DiceElysiumTheme {
        DiceScreen()
    }
}

@Composable
fun DiceScreen(viewModel: DiceViewModel = viewModel()) {
    val uiState by viewModel.diceUIState.collectAsState()
    Box (modifier = Modifier.fillMaxSize()){
        ScrollableBackground(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .align(Alignment.TopCenter),
            movement = uiState.backgroundMovement
        )
        if (!uiState.isRolling) {
            DicesGrid(
                dices = uiState.dices,
                isRolling = false,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Button(
            onClick = {
                viewModel.rollDices()
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(80.dp)
        ) {
            Text(text = "visible: ${uiState.visible}")
        }
        PanelButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(40.dp),
            plusOnClick = { viewModel.addDice() },
            minusOnClick = { viewModel.reduceDice() }
        )
        SuccessEffect(
            visible = uiState.visible,
            modifier = Modifier
                .fillMaxWidth()
                .alpha(0.8f)
                .align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun DicesGrid(
    modifier: Modifier = Modifier,
    dices: List<Dice>,
    isRolling: Boolean
) {

    val diceSize = 64
    val padding = 10
    val colum = ceil(sqrt(dices.size.toFloat())).toInt()
    val width = colum * (diceSize + padding*2)

    LazyVerticalGrid(
        columns = GridCells.Fixed(colum),
        modifier = modifier
            .width(width.dp)
    ) {
        items(items = dices) {
            D6Dice(
                value = it.value,
                color = it.diceColor,
                isRolling = isRolling,
                modifier = Modifier
                    .aspectRatio(1f)
                    .padding(10.dp)
            )
        }
    }
}

@Composable
fun PanelButton(
    modifier: Modifier = Modifier,
    plusOnClick: () -> Unit,
    minusOnClick: () -> Unit
) {
    Row(modifier = modifier) {
        Button(
            onClick = { minusOnClick() }
        ) {
            Text(
                text = "-",
            )
        }
        Spacer(modifier = Modifier.width(64.dp))
        Button(
            onClick = { plusOnClick() }
        ) {
            Text(text = "+")
        }
    }
}