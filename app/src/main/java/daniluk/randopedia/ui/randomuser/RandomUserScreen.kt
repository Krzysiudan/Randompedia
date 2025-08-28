

package daniluk.randopedia.ui.randomuser

import daniluk.randopedia.ui.theme.MyApplicationTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun UsersListScreen(modifier: Modifier = Modifier, viewModel: RandomUserViewModel = hiltViewModel()) {
    val items by viewModel.uiState.collectAsStateWithLifecycle()
    if (items is RandomUserUiState.Success) {
        UsersListScreen(
            items = (items as RandomUserUiState.Success).data,
            modifier = modifier
        )
    }
}

@Composable
internal fun UsersListScreen(
    items: List<String>,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        items.forEach {
            Text("Saved item: $it")
        }
    }
}

// Previews

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    MyApplicationTheme {
        UsersListScreen(listOf("Compose", "Room", "Kotlin"))
    }
}

@Preview(showBackground = true, widthDp = 480)
@Composable
private fun PortraitPreview() {
    MyApplicationTheme {
        UsersListScreen(listOf("Compose", "Room", "Kotlin"))
    }
}
