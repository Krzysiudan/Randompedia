package daniluk.randopedia.ui

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

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            UsersListScreen(
                modifier = Modifier.padding(16.dp),
                onUserClick = { user ->
                    navController.navigate("user_details/${user.id}")
                }
            )
        }
        composable(
            route = "user_details/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId").orEmpty()
            UserDetailsScreen(userId = userId)
        }
    }
}
