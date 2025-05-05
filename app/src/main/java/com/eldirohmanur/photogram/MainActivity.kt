package com.eldirohmanur.photogram

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.eldirohmanur.photogram.presentation.detail.ArtworkDetailScreen
import com.eldirohmanur.photogram.presentation.home.HomeScreen
import com.eldirohmanur.photogram.presentation.saved.SavedImagesScreen
import com.eldirohmanur.photogram.ui.theme.PhotoGramTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PhotoGramTheme {
                ArtGalleryApp()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtGalleryApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    var currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(navBackStackEntry) {
        currentRoute = navBackStackEntry?.destination?.route
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (currentRoute) {
                            Screen.Home.route -> "Photograms"
                            Screen.Saved.route -> "Saved Arts"
                            Screen.Detail.route -> "Artwork Details"
                            else -> "Art Gallery"
                        }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                navigationIcon = {
                    AnimatedVisibility(currentRoute == Screen.Detail.route) {
                        IconButton(
                            onClick = {
                                navController.navigateUp()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = null
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (currentRoute != Screen.Detail.route) {
                BottomNavigation(navController = navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding(), top = paddingValues.calculateTopPadding())
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    navigateToDetail = { artworkId ->
                        val route = Screen.Detail.route.replace(
                            oldValue = "{artworkId}",
                            newValue = "$artworkId"
                        )
                        navController.navigate(route)
                    }
                )
            }
            composable(Screen.Saved.route) {
                SavedImagesScreen(
                    navigateToDetail = { artworkId ->
                        val route = Screen.Detail.route.replace(
                            oldValue = "{artworkId}",
                            newValue = "$artworkId"
                        )
                        navController.navigate(route)
                    }
                )
            }
            composable(
                route = Screen.Detail.route,
                arguments = listOf(
                    navArgument("artworkId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val artworkId = backStackEntry.arguments?.getInt("artworkId") ?: 0
                ArtworkDetailScreen(
                    artworkId = artworkId
                )
            }
        }
    }
}

@Composable
fun BottomNavigation(navController: NavController) {
    val items = listOf(
        Screen.Home,
        Screen.Saved
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.title
                    )
                },
                label = { Text(text = screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

sealed class Screen(
    val route: String,
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    data object Home : Screen("home", "Home", Icons.Default.Home)
    data object Saved : Screen("saved", "Saved", Icons.Default.Favorite)
    data object Detail : Screen("detail/{artworkId}", "Detail", Icons.Default.Home) // Icon not used
}