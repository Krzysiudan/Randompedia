package daniluk.randopedia.ui.list.placeholder

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import daniluk.randopedia.R
import daniluk.randopedia.ui.theme.MyApplicationTheme

@Composable
fun EmptyBookmarksPlaceholder(
    @DrawableRes illustrationRes: Int,
    title: String,
    hint: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Figma-exported illustration
        Image(
            painter = painterResource(illustrationRes),
            contentDescription = null, // decorative
            modifier = Modifier
                .width(160.dp)       // tune to match your asset
                .heightIn(min = 120.dp)
                .padding(top = 8.dp, bottom = 12.dp),
            contentScale = ContentScale.Fit
        )

        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = hint,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true, widthDp = 340, heightDp = 320)
@Composable
private fun EmptyBookmarksSimple_Light() {
    MyApplicationTheme {
        EmptyBookmarksPlaceholder(
            illustrationRes = R.drawable.empty_bookmarks,
            title = "No saved users yet.",
            hint = "Tap the  bookmark to save"
        )
    }
}

@Preview(showBackground = true, widthDp = 340, heightDp = 320, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun EmptyBookmarksSimple_Dark() {
    MyApplicationTheme {
        EmptyBookmarksPlaceholder(
            illustrationRes = R.drawable.empty_bookmarks,
            title = "No saved users yet.",
            hint = "Tap the  bookmark to save"
        )
    }
}