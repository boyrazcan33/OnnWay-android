// app/src/main/java/com/onnway/android/ui/components/DancingTourist.kt
package com.onnway.android.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DancingTourist() {

    // Moonwalk slide animation
    val infiniteTransition = rememberInfiniteTransition(label = "moonwalk")

    val offsetX by infiniteTransition.animateFloat(
        initialValue = 40f,
        targetValue = -40f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offsetX"
    )

    val scaleX by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scaleX"
    )

    // ONNWAY text reveal animation
    val textAlpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 4000
                0f at 0
                0f at 2400
                1f at 2800
                1f at 3600
                0f at 4000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "textAlpha"
    )

    val textOffsetY by infiniteTransition.animateFloat(
        initialValue = 20f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 4000
                20f at 0
                20f at 2400
                0f at 2800
                0f at 3600
                -10f at 4000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "textOffsetY"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        contentAlignment = Alignment.Center
    ) {
        // Character with moonwalk animation
        Image(
            painter = painterResource(
                id = com.onnway.android.R.drawable.mj_moonwalk
            ),
            contentDescription = "Dancing Tourist",
            modifier = Modifier
                .size(280.dp)
                .graphicsLayer {
                    translationX = offsetX
                    this.scaleX = scaleX
                }
        )

        // ONNWAY text above character
        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-60).dp, y = 30.dp)
                .alpha(textAlpha)
                .graphicsLayer {
                    translationY = textOffsetY
                },
            color = Color(0xE6FFFFFF),
            shape = MaterialTheme.shapes.medium,
            shadowElevation = 4.dp
        ) {
            Text(
                text = "ONNWAY",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4ECDC4)
            )
        }
    }
}