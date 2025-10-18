// app/src/main/java/com/onnway/android/ui/components/LoadingModal.kt
package com.onnway.android.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay

@Composable
fun LoadingModal(
    isVisible: Boolean,
    onDismiss: () -> Unit = {}
) {
    if (!isVisible) return

    val messages = listOf(
        "Teaching the map to dance too...",
        "Finding the best spots for you...",
        "Calculating walking distances...",
        "Optimizing your perfect route...",
        "Adding some travel magic...",
        "Almost ready for your adventure!"
    )

    var currentMessageIndex by remember { mutableStateOf(0) }

    LaunchedEffect(isVisible) {
        while (isVisible) {
            delay(2500)
            currentMessageIndex = (currentMessageIndex + 1) % messages.size
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Dancing Tourist
                DancingTourist()

                Spacer(modifier = Modifier.height(24.dp))

                // Loading message
                Text(
                    text = messages[currentMessageIndex],
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .wrapContentHeight(Alignment.CenterVertically)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Loading dots
                LoadingDots()

                Spacer(modifier = Modifier.height(24.dp))

                // Cancel button
                OutlinedButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}

@Composable
fun LoadingDots() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            val infiniteTransition = rememberInfiniteTransition(label = "dot$index")
            val scale by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1400, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart,
                    initialStartOffset = StartOffset(index * 160)
                ),
                label = "dotScale$index"
            )

            Box(
                modifier = Modifier
                    .size(8.dp)
                    .alpha(scale)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            )
        }
    }
}