package com.tiki.diceelysium.ui.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tiki.diceelysium.R
import com.tiki.diceelysium.ui.theme.InActive
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun D6Dice(
    modifier: Modifier = Modifier,
    value: Int,
    color: Color? = null,
    isRolling: Boolean = false
) {
    var diceValue by remember { mutableStateOf(1) }
    LaunchedEffect(isRolling) {
        if (!isRolling) return@LaunchedEffect
        while (true) {
            delay(100)
            diceValue = Random.nextInt(1, 7)
        }
    }
    val resource = when(if (isRolling) diceValue else value) {
        1 -> R.drawable.d6_1
        2 -> R.drawable.d6_2
        3 -> R.drawable.d6_3
        4 -> R.drawable.d6_4
        5 -> R.drawable.d6_5
        else -> R.drawable.d6_6
    }
    Image(
        modifier = if (isRolling) modifier.blur(radius = 2.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded) else modifier,
        painter = painterResource(id = resource),
        colorFilter = color?.let { ColorFilter.tint(it, BlendMode.SrcAtop) },
        contentDescription = "D6Dice",
        contentScale = ContentScale.Fit
    )

}

@Preview
@Composable
fun DicePreview() {
    val size = 128.dp
    D6Dice(
        value = 2,
        color = InActive,
        isRolling = false,
        modifier = Modifier
            .width(size)
            .height(size)
    )
}