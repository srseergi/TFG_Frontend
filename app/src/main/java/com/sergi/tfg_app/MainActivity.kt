package com.sergi.tfg_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.sergi.tfg_app.ui.navigation.NavGraph
import com.sergi.tfg_app.ui.theme.TFG_AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TFG_AppTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}
