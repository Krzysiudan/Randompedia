package daniluk.randopedia.ui.randomuser

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import daniluk.randopedia.R
import daniluk.randopedia.data.model.User
import daniluk.randopedia.ui.theme.MyApplicationTheme
import kotlinx.coroutines.flow.flowOf

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
        onBookmarkClicked = { user -> viewModel.onUserClicked(user) },
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
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier,
    ) {
        LazyColumn {
            items(items.itemCount) { i ->
                items[i]?.let { ui ->
                    val user = ui.user
                    val isBookmarked = ui.isBookmarked
                    ListItem(
                        headlineContent = { Text(user.fullName) },
                        supportingContent = { Text("${user.city}, ${user.country}") },
                        trailingContent = { IconButton(onClick = {
                            // Persist whole user into Room DB on item click via callback
                            onBookmarkClicked(user)
                        })  {
                            Icon(
                                imageVector = Icons.Outlined.Favorite,
                                contentDescription = null,
                                modifier = Modifier,
                                tint = if (isBookmarked) Color.Red else colorResource(R.color.black)
                            )
                        }},
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