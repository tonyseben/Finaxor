package com.tonyseben.finaxor.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

enum class BannerType {
    SUCCESS,
    ERROR
}

@Composable
fun MessageBanner(
    message: String?,
    type: BannerType,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    autoDismissMillis: Long = 3000
) {
    LaunchedEffect(message) {
        if (message != null) {
            delay(autoDismissMillis)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = message != null,
        enter = slideInVertically { -it } + fadeIn(),
        exit = slideOutVertically { -it } + fadeOut(),
        modifier = modifier
    ) {
        val backgroundColor = when (type) {
            BannerType.SUCCESS -> Color(0xFFE8F5E9) // Light green
            BannerType.ERROR -> Color(0xFFFFEBEE) // Light red
        }

        val contentColor = when (type) {
            BannerType.SUCCESS -> Color(0xFF2E7D32) // Dark green
            BannerType.ERROR -> Color(0xFFC62828) // Dark red
        }

        val icon = when (type) {
            BannerType.SUCCESS -> Icons.Default.Check
            BannerType.ERROR -> Icons.Default.Close
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(backgroundColor)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = message ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = contentColor,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
