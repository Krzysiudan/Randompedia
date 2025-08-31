package daniluk.randopedia.ui.common

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush

@Composable
fun shimmerBrush(): Brush {
    val base = MaterialTheme.colorScheme.surfaceVariant
    val colors = listOf(
        base.copy(alpha = 0.6f),
        base.copy(alpha = 0.3f),
        base.copy(alpha = 0.6f)
    )
    val transition = rememberInfiniteTransition(label = "skeleton")
    val translateX by transition.animateFloat(
        initialValue = 0f,
        targetValue = 600f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "translate"
    )
    return Brush.linearGradient(
        colors = colors,
        start = Offset(translateX, 0f),
        end = Offset(translateX + 200f, 0f)
    )
}