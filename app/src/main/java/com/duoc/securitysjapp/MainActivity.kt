package com.duoc.securitysjapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.duoc.securitysjapp.model.AppDatabase
import com.duoc.securitysjapp.respository.UsuarioRepository
import com.duoc.securitysjapp.ui.DetalleCasoScreen
import com.duoc.securitysjapp.ui.ListaCasosScreen
import com.duoc.securitysjapp.ui.LoginScreen
import com.duoc.securitysjapp.ui.RegistroScreen
import com.duoc.securitysjapp.ui.theme.SecuritySJAppTheme
import com.duoc.securitysjapp.viewModel.ListaCasosViewModel
import com.duoc.securitysjapp.viewModel.UsuarioFormViewModel
import com.duoc.securitysjapp.viewModel.UsuarioFormViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializa la Base de Datos Room
        val database = AppDatabase.getDatabase(applicationContext)
        // Crear el Repository pasando el DAO
        val usuarioRepository = UsuarioRepository(database.usuarioDao())
        // Crear el Factory para inyectar el repositorio en el ViewModel
        //
        val usuarioViewModelFactory = UsuarioFormViewModelFactory(usuarioRepository)

        setContent {
            SecuritySJAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White,
                ) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "login") {

                        composable("login") {
                            val authViewModel: UsuarioFormViewModel =
                                viewModel(factory = usuarioViewModelFactory)
                            LoginScreen(
                                viewModel = authViewModel,
                                onLoginExitoso = {
                                    navController.navigate("lista_casos") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                // Redirige a al pantalla de registro
                                onIrARegistro = {
                                    navController.navigate("registro")
                                }
                            )
                        }

                        composable("registro") {
                            val authViewModel: UsuarioFormViewModel =
                                viewModel(factory = usuarioViewModelFactory)
                            RegistroScreen(
                                viewModel = authViewModel,
                                onRegistroExitoso = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable("lista_casos") {
                            val listaCasosViewModel: ListaCasosViewModel = viewModel()
                            ListaCasosScreen(
                                viewModel = listaCasosViewModel,
                                onNavigateBack = { finish() },
                                onCasoClick = { caso ->
                                    navController.navigate("detalle_caso/${caso.id}")
                                }
                            )
                        }

                        composable(
                            route = "detalle_caso/{casoId}",
                            arguments = listOf(navArgument("casoId") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val casoId = backStackEntry.arguments?.getLong("casoId") ?: 0L

                            DetalleCasoScreen(
                                casoId = casoId,
                                onNavigateBack = {
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