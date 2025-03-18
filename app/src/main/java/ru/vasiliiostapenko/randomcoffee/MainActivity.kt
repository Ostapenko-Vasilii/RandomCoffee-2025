package ru.vasiliiostapenko.randomcoffee

import MainScreen
import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import ru.vasiliiostapenko.randomcoffee.DomainLayer.MainActivity.AppState
import ru.vasiliiostapenko.randomcoffee.DomainLayer.MainActivity.MainActivityViewModel
import ru.vasiliiostapenko.randomcoffee.hipemode.HipeMode
import ru.vasiliiostapenko.randomcoffee.ui.screens.ProductInfoScreen.ProductInfoScreen

import ru.vasiliiostapenko.randomcoffee.ui.theme.RandomCoffeeTheme
import ru.vasiliiostapenko.randomcoffee.ui.theme.snackBarColor

class MainActivity : ComponentActivity() {
    lateinit var viewModel: MainActivityViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        regNotifications()
        enableEdgeToEdge()
        setContent {
            val darkTheme = isSystemInDarkTheme()
            viewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {

                        return MainActivityViewModel(darkTheme) as T
                    }
                }
            )

            val openAlertDialog by viewModel.alertState.collectAsState()
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()
            val snackBarHostState by viewModel.snackBarHostState.collectAsState()
            val scope = rememberCoroutineScope()
            val currentProductData by viewModel.productData.collectAsState()

            RandomCoffeeTheme(darkTheme = isDarkTheme) {
                if (openAlertDialog) {
                    HipeModeAlertDialog(
                        onDismissRequest = {
                            viewModel.setAlertState(false)
                        },
                        onConfirmation = {
                            val layout: WindowManager.LayoutParams = window.attributes
                            HipeMode().setBrightness(layout, window)
                            HipeMode().vibrator(baseContext)
                            HipeMode().setAudio(baseContext)
                            viewModel.setAlertState(false)

                        },
                        dialogTitle = stringResource(R.string.hipe_mode),
                        dialogText = stringResource(R.string.hipe_mode_description),
                        icon = ImageVector.vectorResource(id = R.drawable.outline_zone_person_urgent_24)
                    )
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = {
                        SnackbarHost(hostState = snackBarHostState, snackbar = { snackbarData ->
                            Snackbar(
                                snackbarData = snackbarData,
                                containerColor = snackBarColor,
                                contentColor = Color.White,
                                modifier = Modifier.padding(16.dp)
                            )
                        })
                    }) { innerPadding ->
                    var navController = rememberNavController()
                    NavHost(navController, startDestination = MAIN_SCREEN) {
                        composable(MAIN_SCREEN) {
                            MainScreen(
                                padding = innerPadding,
                                isDarkTheme,
                                {
                                    viewModel.setIsDarkThemeState(!isDarkTheme)
                                },
                                { message: String ->
                                    scope.launch {
                                        snackBarHostState.showSnackbar(
                                            message = message,
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                },
                                viewModel.currentPage,
                                { pageId: Int -> viewModel.setCurrentPage(pageId) },
                                viewModel.appState,
                                { product ->
                                    viewModel.setProductData(product)
                                    navController.navigate(PRODUCT_SCREEN + "/${product.id}")
                                }
                            )
                        }

                        composable(
                            PRODUCT_SCREEN + "/{id}",
                            arguments = listOf(navArgument("id") { type = NavType.IntType })
                        ) { stackEntry ->

                            if (currentProductData.id == stackEntry.arguments?.getInt("id")) {
                                ProductInfoScreen(
                                    innerPadding,
                                    isDarkTheme,
                                    {
                                        viewModel.setIsDarkThemeState(!isDarkTheme)
                                    },
                                    currentProductData,
                                    { navController.popBackStack() }
                                )
                            } else {
                                navController.popBackStack()
                            }

                        }
                    }
                }
            }
        }

    }

    private fun regNotifications() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(POST_NOTIFICATIONS), 1)
            }
        }
        FirebaseApp.initializeApp(this)
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM", "Token: $token")
            } else {
                Log.e("FCM", "Error getting token", task.exception)
            }
        }
    }


    companion object {
        const val MAIN_SCREEN = "MAIN_SCREEN"
        const val PRODUCT_SCREEN = "PRODUCT_SCREEN"
    }

    override fun onStart() {
        super.onStart()
        try {
            viewModel.setAppState(AppState.START)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            viewModel.setAppState(AppState.START)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            viewModel.setAppState(AppState.PAUSE)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            viewModel.setAppState(AppState.PAUSE)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Composable
    fun HipeModeAlertDialog(
        onDismissRequest: () -> Unit,
        onConfirmation: () -> Unit,
        dialogTitle: String,
        dialogText: String,
        icon: ImageVector,
    ) {
        AlertDialog(
            icon = {
                Icon(
                    icon,
                    contentDescription = getString(R.string.hipe_mode),
                    modifier = Modifier.size(30.dp)
                )
            },
            title = {
                Text(text = dialogTitle)
            },
            text = {
                Text(text = dialogText)
            },
            onDismissRequest = {
                onDismissRequest()
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirmation()
                    }
                ) {
                    Text(getString(R.string.hipeing), color = MaterialTheme.colorScheme.secondary)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDismissRequest()
                    }
                ) {
                    Text(getString(R.string.miss), color = MaterialTheme.colorScheme.secondary)
                }
            }
        )
    }
}

