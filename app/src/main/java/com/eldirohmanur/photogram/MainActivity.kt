package com.eldirohmanur.photogram

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.eldirohmanur.photogram.presentation.detail.ArtworkDetailScreen
import com.eldirohmanur.photogram.presentation.home.HomeScreen
import com.eldirohmanur.photogram.presentation.saved.SavedImagesScreen
import com.eldirohmanur.photogram.ui.theme.PhotoGramTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable


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


@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
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
                            Screen.Home.toString() -> "Photograms"
                            Screen.Saved.toString() -> "Saved Arts"
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
            Log.e("TESTING", "ArtGalleryApp: ${Screen.Detail.route}")
            AnimatedVisibility(
                visible = currentRoute != Screen.Detail.route,
                enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom),
                exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top)
            ) {
                BottomNavigation(navController = navController)
            }
        }
    ) { paddingValues ->
        SharedTransitionLayout {
            NavHost(
                navController = navController,
                startDestination = Screen.Home,
                modifier = Modifier.padding(
                    bottom = paddingValues.calculateBottomPadding(),
                    top = paddingValues.calculateTopPadding()
                )
            ) {
                composable<Screen.Home> {
                    HomeScreen(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedContentScope = this@composable,
                        navigateToDetail = { artworkId, artworkUrl ->
                            val route = Screen.Detail(artworkId, artworkUrl)
                            navController.navigate(route)
                        }
                    )
                }
                composable<Screen.Saved> {
                    SavedImagesScreen(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedContentScope = this@composable,
                        navigateToDetail = { artworkId, artworkUrl ->
                            val route = Screen.Detail(artworkId, artworkUrl)
                            navController.navigate(route)
                        }
                    )
                }
                composable<Screen.Detail> { backStackEntry ->
                    val args = backStackEntry.toRoute<Screen.Detail>()
                    ArtworkDetailScreen(
                        artworkId = args.artworkId,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedContentScope = this@composable,
                        artworkUrl = args.artworkUrl
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavigation(navController: NavController) {
    val items = listOf(
        Screen.Home to Icons.Default.Home,
        Screen.Saved to Icons.Default.Favorite
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = screen.second,
                        contentDescription = screen.first.title
                    )
                },
                label = { Text(text = screen.first.title) },
                selected = currentRoute == screen.first.toString(),
                onClick = {
                    if (currentRoute != screen.first.toString()) {
                        navController.navigate(screen.first) {
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

@Serializable
sealed class Screen(val title: String) {
    @Serializable
    data object Home : Screen("Home") {
        override fun toString(): String {
            return this::class.simpleName.orEmpty()
        }
    }

    @Serializable
    data object Saved : Screen("Saved") {
        override fun toString(): String {
            return this::class.simpleName.orEmpty()
        }
    }

    @Serializable
    data class Detail(val artworkId: Int, val artworkUrl: String) : Screen("Detail") {
        companion object {
            val route = "com.eldirohmanur.photogram.Screen.Detail/{title}/{artworkId}/{artworkUrl}"
        }
    }
}