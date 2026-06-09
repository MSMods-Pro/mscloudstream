package com.example.ui.navigation

import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.screens.DetailScreen
import com.example.ui.screens.ExtensionsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PlayerScreen
import com.example.ui.screens.SearchScreen

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector?) {
    object Home : Screen("home", "Home", Icons.Filled.Home)
    object Search : Screen("search", "Search", Icons.Filled.Search)
    object Extensions : Screen("extensions", "Extensions", Icons.Filled.List)
    object Detail : Screen("detail/{title}/{posterUrl}/{url}/{providerName}", "Detail", null) {
        fun createRoute(title: String, posterUrl: String, url: String, providerName: String): String {
            val encodedTitle = Uri.encode(title)
            val encodedPoster = Uri.encode(posterUrl)
            val encodedUrl = Uri.encode(url)
            val encodedProvider = Uri.encode(providerName)
            return "detail/$encodedTitle/$encodedPoster/$encodedUrl/$encodedProvider"
        }
    }
    object Player : Screen("player/{url}/{providerName}", "Player", null) {
        fun createRoute(url: String, providerName: String): String {
            return "player/${Uri.encode(url)}/${Uri.encode(providerName)}"
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val items = listOf(
        Screen.Home,
        Screen.Search,
        Screen.Extensions
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route

    // Define routes where bottom bar should be hidden
    val hideBottomBarRoutes = listOf(Screen.Detail.route, Screen.Player.route)
    val shouldShowBottomBar = currentRoute !in hideBottomBarRoutes

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.background,
                    modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    items.forEach { screen ->
                        val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            icon = { screen.icon?.let { Icon(it, contentDescription = screen.title) } },
                            label = { Text(screen.title) },
                            selected = isSelected,
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.background,
                                selectedIconColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                selectedTextColor = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = Screen.Home.route, Modifier.padding(innerPadding)) {
            composable(Screen.Home.route) { HomeScreen(navController) }
            composable(Screen.Search.route) { SearchScreen() }
            composable(Screen.Extensions.route) { ExtensionsScreen() }
            
            composable(
                route = Screen.Detail.route,
                arguments = listOf(
                    navArgument("title") { type = NavType.StringType },
                    navArgument("posterUrl") { type = NavType.StringType },
                    navArgument("url") { type = NavType.StringType },
                    navArgument("providerName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val title = Uri.decode(backStackEntry.arguments?.getString("title") ?: "")
                val posterUrl = Uri.decode(backStackEntry.arguments?.getString("posterUrl") ?: "")
                val url = Uri.decode(backStackEntry.arguments?.getString("url") ?: "")
                val providerName = Uri.decode(backStackEntry.arguments?.getString("providerName") ?: "")
                DetailScreen(navController, title, posterUrl, url, providerName)
            }
            
            composable(
                route = Screen.Player.route,
                arguments = listOf(
                    navArgument("url") { type = NavType.StringType },
                    navArgument("providerName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val url = Uri.decode(backStackEntry.arguments?.getString("url") ?: "")
                val providerName = Uri.decode(backStackEntry.arguments?.getString("providerName") ?: "")
                PlayerScreen(navController, url, providerName)
            }
        }
    }
}
