package com.sergi.tfg_app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.sergi.tfg_app.data.local.TokenDataStore
import com.sergi.tfg_app.data.remote.RetrofitClient
import com.sergi.tfg_app.data.repository.AuthRepository
import com.sergi.tfg_app.data.repository.CvRepository
import com.sergi.tfg_app.ui.components.BottomNavBar
import com.sergi.tfg_app.ui.screens.cvdetail.CvDetailScreen
import com.sergi.tfg_app.ui.screens.cvdetail.CvDetailViewModel
import com.sergi.tfg_app.ui.screens.gallery.GalleryScreen
import com.sergi.tfg_app.ui.screens.gallery.GalleryViewModel
import com.sergi.tfg_app.ui.screens.home.HomeScreen
import com.sergi.tfg_app.ui.screens.home.HomeViewModel
import com.sergi.tfg_app.ui.screens.login.LoginScreen
import com.sergi.tfg_app.ui.screens.login.LoginViewModel
import com.sergi.tfg_app.ui.screens.profile.ProfileScreen
import com.sergi.tfg_app.ui.screens.profile.ProfileViewModel
import com.sergi.tfg_app.ui.screens.register.RegisterScreen
import com.sergi.tfg_app.ui.screens.register.RegisterViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@Composable
fun NavGraph(navController: NavHostController) {
    val context = LocalContext.current
    val dataStore = remember { TokenDataStore(context) }
    val authRepository = remember { AuthRepository(RetrofitClient.authApi, dataStore) }

    // Token provider para el AuthInterceptor
    val tokenProvider: () -> String? = {
        runBlocking { dataStore.getAccessToken().first() }
    }

    // Crear CvApi autenticada y CvRepository
    val cvApi = remember { RetrofitClient.createAuthenticatedCvApi(tokenProvider) }
    val cvRepository = remember { CvRepository(cvApi, dataStore, context) }

    // Crear ViewModels
    val loginViewModel = remember { LoginViewModel(authRepository) }
    val registerViewModel = remember { RegisterViewModel(authRepository) }
    val profileViewModel = remember { ProfileViewModel(authRepository) }
    val homeViewModel = remember { HomeViewModel(cvRepository) }
    val galleryViewModel = remember { GalleryViewModel(cvRepository) }

    // Verificar si hay sesión guardada
    val isLoggedIn by authRepository.isLoggedIn().collectAsState(initial = false)

    // Redirigir a Home si ya hay sesión
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn && navController.currentDestination?.route == Routes.Login.route) {
            navController.navigate(Routes.Home.route) {
                popUpTo(Routes.Login.route) { inclusive = true }
            }
        }
    }

    // Determinar ruta actual para BottomNavBar
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Rutas que muestran el BottomNavBar
    val bottomNavRoutes = listOf(
        Routes.Home.route,
        Routes.Gallery.route,
        Routes.Profile.route
    )
    val showBottomBar = currentRoute in bottomNavRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        if (route != currentRoute) {
                            navController.navigate(route) {
                                // Evitar múltiples copias de la misma pantalla en el back stack
                                popUpTo(Routes.Home.route) {
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
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Routes.Login.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Routes.Login.route) {
                LoginScreen(
                    viewModel = loginViewModel,
                    onLoginSuccess = {
                        navController.navigate(Routes.Home.route) {
                            popUpTo(Routes.Login.route) { inclusive = true }
                        }
                    },
                    onRegisterClick = {
                        navController.navigate(Routes.Register.route)
                    }
                )
            }

            composable(Routes.Register.route) {
                RegisterScreen(
                    viewModel = registerViewModel,
                    onRegisterSuccess = {
                        navController.navigate(Routes.Home.route) {
                            popUpTo(Routes.Login.route) { inclusive = true }
                        }
                    },
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Routes.Home.route) {
                HomeScreen(
                    viewModel = homeViewModel,
                    onCvReady = { cvId ->
                        navController.navigate(Routes.CvDetail.createRoute(cvId))
                    }
                )
            }

            composable(Routes.Gallery.route) {
                GalleryScreen(
                    viewModel = galleryViewModel,
                    onCvClick = { cvId ->
                        navController.navigate(Routes.CvDetail.createRoute(cvId))
                    }
                )
            }

            composable(Routes.Profile.route) {
                ProfileScreen(
                    viewModel = profileViewModel,
                    onLogoutClick = {
                        navController.navigate(Routes.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = Routes.CvDetail.route,
                arguments = listOf(
                    navArgument("cvId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val cvId = backStackEntry.arguments?.getString("cvId") ?: ""
                val cvDetailViewModel = remember(cvId) {
                    CvDetailViewModel(cvRepository, cvId)
                }
                CvDetailScreen(
                    viewModel = cvDetailViewModel,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
