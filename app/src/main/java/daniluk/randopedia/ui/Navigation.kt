package daniluk.randopedia.ui

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import daniluk.randopedia.ui.list.UsersListScreen
import daniluk.randopedia.ui.details.UserDetailsScreen
import daniluk.randopedia.domain.model.User
import daniluk.randopedia.ui.start.StartScreen
import kotlinx.serialization.json.Json

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "start") {
        composable("start") {
            StartScreen(onStart = { navController.navigate("main") })
        }
        composable("main") {
            UsersListScreen(
                onUserClick = { user ->
                    val json = Json.encodeToString(User.serializer(), user)
                    val encoded = Uri.encode(json)
                    navController.navigate("user_details/$encoded")
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "user_details/{user}",
            arguments = listOf(navArgument("user") { type = NavType.StringType })
        ) { backStackEntry ->
            UserDetailsScreen(onBack = { navController.popBackStack() })
        }
        // Parameterless route to allow opening details without passing a user (fallback UI)
        composable("user_details") {
            UserDetailsScreen(onBack = { navController.popBackStack() })
        }
    }
}
