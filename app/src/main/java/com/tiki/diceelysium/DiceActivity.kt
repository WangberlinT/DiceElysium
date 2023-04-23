package com.tiki.diceelysium

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.*
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
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tiki.diceelysium.model.Dice
import com.tiki.diceelysium.ui.theme.DiceElysiumTheme
import com.tiki.diceelysium.ui.widgets.D6Dice
import com.tiki.diceelysium.ui.widgets.ScrollableBackground
import com.tiki.diceelysium.ui.widgets.SuccessEffect
import com.tiki.diceelysium.viewmodel.DiceViewModel
import kotlinx.coroutines.launch
import kotlin.math.*

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
        AnimatedVisibility(
            visible = !uiState.isRolling,
            enter = EnterTransition.None,
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.Center)
                .dragToRelease {
                    viewModel.rollDices()
                }
        ) {
            DicesGrid(
                dices = uiState.dices,
                isRolling = false
            )
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
        userScrollEnabled = false,
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

fun Modifier.dragToRelease(
    maxOffsetY: Dp = 100.dp,
    onRelease: () -> Unit
): Modifier = composed {
    val offsetY = remember { Animatable(0f, Float.VectorConverter) }
    val scope = rememberCoroutineScope()
    pointerInput(Unit) {
        detectDragGestures(
            onDragEnd = {
                scope.launch { offsetY.animateTo(0f) }
                onRelease()
            }
        ) { change, dragAmount ->
            if (dragAmount.y <= 0f) return@detectDragGestures
            change.consume()
            scope.launch {
                val target = min(offsetY.value + dragAmount.y, maxOffsetY.toPx())
                offsetY.snapTo(target)
            }
        }

    }
    .offset { IntOffset(0, offsetY.value.roundToInt()) }
}