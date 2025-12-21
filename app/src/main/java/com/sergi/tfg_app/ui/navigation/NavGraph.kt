package com.sergi.tfg_app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sergi.tfg_app.ui.screens.cvdetail.CvDetailScreen
import com.sergi.tfg_app.ui.screens.gallery.GalleryScreen
import com.sergi.tfg_app.ui.screens.home.HomeScreen
import com.sergi.tfg_app.ui.screens.login.LoginScreen
import com.sergi.tfg_app.ui.screens.profile.ProfileScreen
import com.sergi.tfg_app.ui.screens.register.RegisterScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.Login.route
    ) {
        composable(Routes.Login.route) {
            LoginScreen(
                onLoginClick = {
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
                onBackClick = {
                    navController.popBackStack()
                },
                onRegisterClick = {
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
