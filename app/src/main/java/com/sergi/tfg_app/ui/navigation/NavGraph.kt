package com.sergi.tfg_app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sergi.tfg_app.data.local.TokenDataStore
import com.sergi.tfg_app.data.remote.RetrofitClient
import com.sergi.tfg_app.data.repository.AuthRepository
import com.sergi.tfg_app.ui.screens.cvdetail.CvDetailScreen
import com.sergi.tfg_app.ui.screens.gallery.GalleryScreen
import com.sergi.tfg_app.ui.screens.home.HomeScreen
import com.sergi.tfg_app.ui.screens.login.LoginScreen
import com.sergi.tfg_app.ui.screens.login.LoginViewModel
import com.sergi.tfg_app.ui.screens.profile.ProfileScreen
import com.sergi.tfg_app.ui.screens.profile.ProfileViewModel
import com.sergi.tfg_app.ui.screens.register.RegisterScreen
import com.sergi.tfg_app.ui.screens.register.RegisterViewModel

@Composable
fun NavGraph(navController: NavHostController) {
    // Crear dependencias
    val context = LocalContext.current
    val dataStore = remember { TokenDataStore(context) }
    val authRepository = remember { AuthRepository(RetrofitClient.authApi, dataStore) }

    // Crear ViewModels
    val loginViewModel = remember { LoginViewModel(authRepository) }
    val registerViewModel = remember { RegisterViewModel(authRepository) }
    val profileViewModel = remember { ProfileViewModel(authRepository) }

    // Verificar si hay sesión guardada
    val isLoggedIn by authRepository.isLoggedIn().collectAsState(initial = false)

    // Redirigir a Home si ya hay sesión (solo una vez al inicio)
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn && navController.currentDestination?.route == Routes.Login.route) {
            navController.navigate(Routes.Home.route) {
                popUpTo(Routes.Login.route) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.Login.route
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
                onProfileClick = {
                    navController.navigate(Routes.Profile.route)
                },
                onGalleryClick = {
                    navController.navigate(Routes.Gallery.route)
                }
            )
        }

        composable(Routes.Gallery.route) {
            GalleryScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onCvClick = { cvTitle ->
                    navController.navigate(Routes.CvDetail.createRoute(cvTitle))
                }
            )
        }

        composable(
            route = Routes.CvDetail.route,
            arguments = listOf(
                navArgument("cvTitle") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val cvTitle = backStackEntry.arguments?.getString("cvTitle") ?: ""
            CvDetailScreen(
                cvTitle = cvTitle,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.Profile.route) {
            ProfileScreen(
                viewModel = profileViewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onLogoutClick = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
