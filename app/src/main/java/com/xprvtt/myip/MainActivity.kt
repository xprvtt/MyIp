package com.xprvtt.myip

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.xprvtt.myip.ui.theme.MyIpTheme
import com.xprvtt.myip.window.addprofile.AddProfileScreen
import com.xprvtt.myip.window.HelpScreen
import com.xprvtt.myip.window.MainScreen
import com.xprvtt.myip.window.SettingsScreen
import kotlinx.coroutines.launch

//--------------------------------------------------------------------------------------------------

class MainActivity : ComponentActivity() {
    private lateinit var settingsDataStore: SettingsDataStore

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        settingsDataStore = SettingsDataStore(applicationContext)
        lifecycleScope.launch{ settingsDataStore.initializeProfiles() }

        setContent {
            MyIpTheme {
                val navController = rememberNavController()

                NavHost(navController = navController,startDestination = "main", modifier = Modifier.fillMaxSize()) {

                    composable("main") {
                        MainScreen(
                            settingsDataStore = settingsDataStore,
                            onOpenSettings = {
                                navController.navigate("settings")
                            }
                        )
                    }

                    composable("settings") {
                        SettingsScreen(
                            settingsDataStore = settingsDataStore,
                            onBack = {
                                navController.popBackStack()
                            },
                            onOpenAddProfile = {
                                navController.navigate("addProfile")
                            },
                            onOpenHelp = {
                                navController.navigate("help")
                            },
                            onOpenEditProfile = { profileId ->
                                navController.navigate("editProfile/$profileId")
                            }
                        )
                    }

                    composable("addProfile") {
                        AddProfileScreen(
                            settingsDataStore = settingsDataStore,
                            onBack = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable("help") {
                        HelpScreen(
                            onBack = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable(
                        route = "editProfile/{profileId}",
                        arguments = listOf(
                            navArgument("profileId") {
                                type = NavType.StringType
                            }
                        )
                    ) { backStackEntry ->

                        val profileId = backStackEntry.arguments?.getString("profileId")
                        val profiles by settingsDataStore.profiles.collectAsState( initial = emptyList() )
                        val currentProfile = profiles.find { it.id == profileId }

                        if (currentProfile != null) {
                            AddProfileScreen(
                                settingsDataStore = settingsDataStore,
                                profile = currentProfile,
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

//--------------------------------------------------------------------------------------------------
