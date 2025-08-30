package daniluk.randopedia.ui.randomuser

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import daniluk.randopedia.R
import daniluk.randopedia.data.model.User
import daniluk.randopedia.ui.theme.MyApplicationTheme
import kotlinx.coroutines.flow.flowOf
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersListScreen(
    modifier: Modifier = Modifier,
    viewModel: RandomUserViewModel = hiltViewModel(),
    onUserClick: (User) -> Unit = {}
) {
    val items = viewModel.uiPagingFlow.collectAsLazyPagingItems()
    val isRefreshing = items.loadState.refresh is LoadState.Loading

    UsersListScreen(
        items = items,
        modifier = modifier,
        isRefreshing = isRefreshing,
        onRefresh = { items.refresh() },
        onBookmarkClicked = { user -> viewModel.onBookmarkClicked(user) },
        onUserClick = onUserClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun UsersListScreen(
    items: LazyPagingItems<UserUiModel>,
    modifier: Modifier = Modifier,
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
    onBookmarkClicked: (User) -> Unit = {},
    onUserClick: (User) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Users") }
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = modifier.padding(paddingValues),
        ) {
            val showSkeleton = (items.loadState.refresh is LoadState.Loading || isRefreshing) && items.itemCount == 0
            LazyColumn {
                if (showSkeleton) {
                    items(8) {
                        SkeletonUserListItem()
                        Divider()
                    }
                } else {
                    items(items.itemCount) { i ->
                        items[i]?.let { ui ->
                            val user = ui.user
                            ListItem(
                                leadingContent = {
                                    AsyncImage(
                                        model = user.avatarUrl,
                                        contentDescription = "Avatar",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                    )
                                },
                                headlineContent = { Text(user.fullName) },
                                supportingContent = { Text("${user.city}, ${user.country}") },
                                trailingContent = {
                                    IconButton(onClick = {
                                        onBookmarkClicked(user)
                                    }) {
                                        Icon(
                                            painter = if (ui.isBookmarked) painterResource(R.drawable.ic_bookmark_filled) else painterResource(
                                                R.drawable.ic_bookmark
                                            ),
                                            tint = if (ui.isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            contentDescription = "Bookmark",
                                        )

                                    }
                                },
                                modifier = Modifier.clickable { onUserClick(user) }
                            )
                            Divider()
                        }
                    }
                    when (val s = items.loadState.append) {
                        is LoadState.Loading -> item { CircularProgressIndicator(Modifier.padding(16.dp)) }
                        is LoadState.Error -> item { Text("Load more failed: ${s.error.localizedMessage}") }
                        else -> Unit
                    }
                }
            }
        }
    }
}


/**
 * Helper to create LazyPagingItems from a static list for previews.
 */
@Composable
private fun rememberPagingItems(sample: List<UserUiModel>): LazyPagingItems<UserUiModel> {
    val flow = remember(sample) { flowOf(PagingData.from(sample)) }
    return flow.collectAsLazyPagingItems()
}

/** Sample data used across previews */
private fun sampleUsers(count: Int = 5): List<User> =
    List(count) { i ->
        User(
            id = i.toString(),
            fullName = "John Doe $i",
            email = "john.doe$i@example.com",
            country = "USA",
            city = "New York",
            age = 30 + (i % 7),
            avatarUrl = "https://randomuser.me/api/portraits/thumb/men/${(i % 90)}.jpg",
            photoUrl = "https://randomuser.me/api/portraits/men/${(i % 90)}.jpg"
        )
    }

private fun sampleUiUsers(count: Int = 5): List<UserUiModel> =
    sampleUsers(count).mapIndexed { i, u -> UserUiModel(user = u, isBookmarked = (i % 2 == 0)) }

/**
 * Simple shimmer brush for skeleton placeholders.
 */
@Composable
private fun shimmerBrush(): Brush {
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
        start = androidx.compose.ui.geometry.Offset(translateX, 0f),
        end = androidx.compose.ui.geometry.Offset(translateX + 200f, 0f)
    )
}

@Composable
private fun SkeletonUserListItem() {
    val brush = shimmerBrush()
    ListItem(
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(brush)
            )
        },
        headlineContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(16.dp)
                    .background(brush)
            )
        },
        supportingContent = {
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .height(12.dp)
                    .background(brush)
            )
        },
        trailingContent = {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(brush)
            )
        }
    )
}

/** Default phone-size, light theme */
@Preview(showBackground = true)
@Composable
private fun UsersList_Light_Preview() {
    MyApplicationTheme {
        UsersListScreen(
            items = rememberPagingItems(sampleUiUsers(8))
        )
    }
}

/** Phone-size, dark theme */
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun UsersList_Dark_Preview() {
    MyApplicationTheme {
        UsersListScreen(
            items = rememberPagingItems(sampleUiUsers(8))
        )
    }
}

/** Loading state simulated via isRefreshing flag */
@Preview(showBackground = true)
@Composable
private fun UsersList_Loading_Preview() {
    MyApplicationTheme {
        UsersListScreen(
            items = rememberPagingItems(emptyList()),
            isRefreshing = true,
            onRefresh = {}
        )
    }
}

/** Empty list */
@Preview(showBackground = true)
@Composable
private fun UsersList_Empty_Preview() {
    MyApplicationTheme {
        UsersListScreen(
            items = rememberPagingItems(emptyList())
        )
    }
}