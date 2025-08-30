package daniluk.randopedia.ui

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import daniluk.randopedia.ui.randomuser.UsersListScreen
import daniluk.randopedia.ui.randomuser.UserDetailsScreen
import daniluk.randopedia.data.model.User
import kotlinx.serialization.json.Json

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            UsersListScreen(
                modifier = Modifier.padding(16.dp),
                onUserClick = { user ->
                    val json = Json.encodeToString(User.serializer(), user)
                    val encoded = Uri.encode(json)
                    navController.navigate("user_details/$encoded")
                }
            )
        }
        composable(
            route = "user_details/{user}",
            arguments = listOf(navArgument("user") { type = NavType.StringType })
        ) { backStackEntry ->
            // Decoding here is optional if ViewModel reads from SavedStateHandle; keep simple UI invocation
            UserDetailsScreen()
        }
    }
}
