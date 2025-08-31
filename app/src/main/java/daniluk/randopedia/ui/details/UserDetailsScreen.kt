@file:OptIn(ExperimentalMaterial3Api::class)

package daniluk.randopedia.ui.details

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import daniluk.randopedia.R
import daniluk.randopedia.ui.theme.MyApplicationTheme

data class UserDetailsUi(
    val photoUrl: String,
    val name: String,
    val email: String,
    val phone: String,
    val address: String,
    val isBookmarked: Boolean
)

@Composable
fun UserDetailsScreen(
    viewModel: UserDetailsViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
) {
    val ui by viewModel.ui.collectAsState()
    UserDetailsScreen(
        ui = ui,
        onBack = onBack,
        onToggleBookmark = { viewModel.toggleBookmark() }
    )
}

@Composable
fun UserDetailsScreen(
    ui: UserDetailsUi,
    onBack: () -> Unit,
    onToggleBookmark: () -> Unit,
) {
    val bg = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)

    Scaffold(
        containerColor = bg,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconToggleButton(
                        checked = ui.isBookmarked,
                        onCheckedChange = { onToggleBookmark() }
                    ) {
                        Icon(
                            painter = if (ui.isBookmarked) painterResource(R.drawable.ic_bookmark_filled) else painterResource(
                                R.drawable.ic_bookmark
                            ),
                            tint = if (ui.isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            contentDescription = "Bookmark"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            // Avatar + small bookmark badge on the avatar
            Box(contentAlignment = Alignment.BottomEnd) {
                // Provide placeholder and error painters for better UX during loading/failure
                val placeholderPainter =
                    rememberVectorPainter(Icons.Outlined.AccountCircle)
                AsyncImage(
                    model = ui.photoUrl,
                    contentDescription = "User avatar",
                    contentScale = ContentScale.Crop,
                    placeholder = placeholderPainter,
                    error = placeholderPainter,
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                )
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp,
                    modifier = Modifier
                        .offset(x = 6.dp, y = 6.dp) // small float outside the circle
                        .size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = if (ui.isBookmarked) painterResource(R.drawable.ic_bookmark_filled) else painterResource(
                                R.drawable.ic_bookmark
                            ),
                            tint = if (ui.isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            contentDescription = "Bookmark"
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = ui.name,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            DetailRow(
                icon = Icons.Outlined.Call,
                text = ui.phone
            )
            Spacer(Modifier.height(12.dp))
            DetailRow(
                icon = Icons.Outlined.Email,
                text = ui.email
            )
            Spacer(Modifier.height(12.dp))
            DetailRow(
                icon = Icons.Outlined.Place,
                text = ui.address
            )
        }
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    text: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            modifier = Modifier.weight(1f)
        )
    }
}

/* -------------------- PREVIEWS -------------------- */

private fun sampleUi(bookmarked: Boolean) = UserDetailsUi(
    photoUrl = "https://randomuser.me/api/portraits/women/44.jpg",
    name = "Floyd Miles",
    phone = "(319) 555-0115",
    address = "4517 Washington Ave. Manchester,\nKentucky 39495",
    email = "john.c.calhoun@examplepetstore.com",
    isBookmarked = bookmarked
)

@Preview(showBackground = true)
@Composable
private fun Details_Light_NotBookmarked() {
    MyApplicationTheme {
        UserDetailsScreen(
            ui = sampleUi(bookmarked = false),
            onBack = {},
            onToggleBookmark = {}
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun Details_Dark_Bookmarked() {
    MyApplicationTheme {
        UserDetailsScreen(
            ui = sampleUi(bookmarked = true),
            onBack = {},
            onToggleBookmark = {}
        )
    }
}
