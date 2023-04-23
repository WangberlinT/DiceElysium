package com.tiki.diceelysium.ui.widgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.tiki.diceelysium.R
import com.tiki.diceelysium.model.Movement
import kotlinx.coroutines.launch

@Composable
fun SuccessEffect(modifier: Modifier = Modifier, visible: Boolean) {
    AnimatedVisibility(
        visible = visible,
        enter = EnterTransition.None,
        exit = fadeOut(),
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = R.drawable.green_success),
            contentDescription = "success",
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
fun ScrollableBackground(
    modifier: Modifier = Modifier,
    movement: Movement
) {
    val image = ImageBitmap.imageResource(R.drawable.bg_film)
    val animatedOffsetY = remember { Animatable(0, Int.VectorConverter) }
    val animatedBlurY = remember { Animatable(0, Int.VectorConverter) }

    val increment = (movement.offsetYPercent * image.height).toInt()
    LaunchedEffect(movement) {
        if (increment == 0) return@LaunchedEffect
        launch {
            animatedOffsetY.animateTo(
                targetValue = animatedOffsetY.value + increment,
                animationSpec = tween(
                    movement.durationMS, easing = FastOutLinearInEasing)
            )
            animatedOffsetY.snapTo(animatedOffsetY.value % image.height)
        }
        if (!movement.applyBlur) return@LaunchedEffect
        launch {
            animatedBlurY.animateTo(
                targetValue = 15,
                animationSpec = tween(movement.durationMS)
            )
            animatedBlurY.snapTo(0)
        }

    }
    val blurY = animatedBlurY.value
    Canvas(
        modifier = modifier.blur(
            radiusX = if (movement.applyBlur) 0.1.dp else 0.dp,
            radiusY = if (movement.applyBlur) blurY.dp else 0.dp
        ),
    ) {
        val scale = size.width / image.width
        val sWidth = size.width.toInt()
        val sHeight = image.height * scale.toInt()

        val safeOffsetY = animatedOffsetY.value % image.height
        val visibleImageHeight = image.height - safeOffsetY
        val blankHeight = size.height - visibleImageHeight
        drawImage(
            image = image,
            srcOffset = IntOffset(0, safeOffsetY),
            dstSize = IntSize(sWidth, sHeight),
        )
        if (blankHeight > 0) {
            drawImage(
                image = image,
                dstOffset = IntOffset(0, visibleImageHeight),
                dstSize = IntSize(sWidth, sHeight),
            )
        }
    }
}

@Preview
@Composable
fun BackgroundPreview() {
    Box {
        ScrollableBackground(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .border(2.dp, Color.Red),
            Movement(offsetYPercent = 10.5f, applyBlur = true, durationMS = 5000)
        )
    }
}