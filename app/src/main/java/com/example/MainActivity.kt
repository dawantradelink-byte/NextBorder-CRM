package com.example

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.data.AppDatabase
import com.example.ui.components.GlassBackground
import com.example.ui.components.SplashScreen
import com.example.ui.screens.AgentDashboardScreen
import com.example.ui.screens.AnalyticsDashboardScreen
import com.example.ui.screens.AppLockOverlayScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.UniversityDetailScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.util.BiometricSecurityManager
import com.example.viewmodel.CrmViewModel

class MainActivity : FragmentActivity() {

    val securityManager by lazy { BiometricSecurityManager(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize AIOS Phase 4 Architecture Engines
        com.example.core.ai.CoreAiAgentInitializer.initializeDefaultAgents()
        com.example.core.log.AiosLogger.info("MainActivity", "NextBorder AIOS Phase 4 Core Engines Initialized.")

        // Automatically start Background Manager (Android WorkManager)
        com.example.util.BackgroundSyncManager.startBackgroundManager(applicationContext)

        val db = AppDatabase.getDatabase(this)
        val dao = db.universityDao()
        val todoDao = db.todoDao()
        val agentDao = db.agentDao()
        val reminderDao = db.reminderDao()
        val researchDao = db.researchDao()
        val studentDao = db.studentDao()
        val executiveMemoryDao = db.executiveMemoryDao()

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(CrmViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return CrmViewModel(application, dao, todoDao, agentDao, securityManager, reminderDao, researchDao, studentDao, executiveMemoryDao) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }

        setContent {
            val viewModel: CrmViewModel = viewModel(factory = factory)
            val settings by viewModel.businessSettings.collectAsState()
            val isLocked by securityManager.isLocked.collectAsState()
            var showSplash by remember { mutableStateOf(true) }

            MyApplicationTheme(darkTheme = settings.isDarkTheme) {
                val navController = rememberNavController()

                GlassBackground {
                    if (showSplash) {
                        SplashScreen(
                            onSplashFinished = { showSplash = false }
                        )
                    } else if (isLocked) {
                        AppLockOverlayScreen(
                            securityManager = securityManager,
                            onUnlockSuccess = {
                                securityManager.setUnlocked()
                            }
                        )
                    } else {
                        NavHost(navController = navController, startDestination = "dashboard") {
                            composable("dashboard") {
                                DashboardScreen(
                                    viewModel = viewModel,
                                    onNavigateToUniversity = { id ->
                                        if (id == null) {
                                            navController.navigate("university")
                                        } else {
                                            navController.navigate("university?id=$id")
                                        }
                                    },
                                    onNavigateToAgents = {
                                        navController.navigate("agents")
                                    },
                                    onNavigateToSettings = {
                                        navController.navigate("settings")
                                    },
                                    onNavigateToAnalytics = {
                                        navController.navigate("analytics")
                                    }
                                )
                            }
                            composable("analytics") {
                                AnalyticsDashboardScreen(
                                    viewModel = viewModel,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                            composable(
                                route = "university?id={id}",
                                arguments = listOf(navArgument("id") { type = NavType.StringType; nullable = true })
                            ) { backStackEntry ->
                                val idParam = backStackEntry.arguments?.getString("id")
                                val uniId = idParam?.toIntOrNull()
                                UniversityDetailScreen(
                                    uniId = uniId,
                                    viewModel = viewModel,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                            composable("agents") {
                                AgentDashboardScreen(
                                    viewModel = viewModel,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                            composable("settings") {
                                SettingsScreen(
                                    viewModel = viewModel,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        securityManager.onAppForegrounded()
    }

    override fun onPause() {
        super.onPause()
        securityManager.onAppBackgrounded()
    }
}
