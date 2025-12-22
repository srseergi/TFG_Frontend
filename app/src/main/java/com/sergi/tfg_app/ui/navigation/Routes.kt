package com.sergi.tfg_app.ui.navigation

sealed class Routes(val route: String) {
    object Login : Routes("login")
    object Register : Routes("register")
    object Home : Routes("home")
    object Gallery : Routes("gallery")
    object Profile : Routes("profile")
    object CvDetail : Routes("cv_detail/{cvId}") {
        fun createRoute(cvId: String) = "cv_detail/$cvId"
    }
}
