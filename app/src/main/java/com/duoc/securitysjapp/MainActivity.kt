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
import com.duoc.securitysjapp.repository.CasoRepository
import com.duoc.securitysjapp.repository.UsuarioRepository
import com.duoc.securitysjapp.ui.DetalleCasoScreen
import com.duoc.securitysjapp.ui.CierreCasoScreen
import com.duoc.securitysjapp.ui.ListaCasosScreen
import com.duoc.securitysjapp.ui.LoginScreen
import com.duoc.securitysjapp.ui.NuevoCasoScreen
import com.duoc.securitysjapp.ui.ProfileScreen
import com.duoc.securitysjapp.ui.RegistroScreen
import com.duoc.securitysjapp.ui.theme.SecuritySJAppTheme
import com.duoc.securitysjapp.viewModel.CasoViewModel
import com.duoc.securitysjapp.viewModel.DetalleCasoViewModel
import com.duoc.securitysjapp.viewModel.ListaCasosViewModel
import com.duoc.securitysjapp.viewModel.ProfileViewModel
import com.duoc.securitysjapp.viewModel.ProfileViewModelFactory
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
        val casoRepository = CasoRepository(database.casoDao())
        // Factories para inyectar el repositorio en los ViewModels
        val usuarioViewModelFactory = UsuarioFormViewModelFactory(usuarioRepository)
        val profileViewModelFactory = ProfileViewModelFactory(usuarioRepository)

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
                                onLoginExitoso = { usuarioId ->
                                    // Se pasa el id del usuario para poder cargar su perfil después
                                    navController.navigate("lista_casos/$usuarioId") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                // Redirige a la pantalla de registro
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

                        composable(
                            route = "lista_casos/{usuarioId}",
                            arguments = listOf(navArgument("usuarioId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val usuarioId = backStackEntry.arguments?.getInt("usuarioId") ?: 0
                            val listaCasosViewModel: ListaCasosViewModel = viewModel(
                                factory = ListaCasosViewModel.provideFactory(casoRepository, usuarioId)
                            )
                            ListaCasosScreen(
                                viewModel = listaCasosViewModel,
                                onNavigateBack = { finish() },
                                onCasoClick = { caso ->
                                    navController.navigate("detalle_caso/${caso.id}")
                                },
                                // El botón "Perfil" de la barra inferior llama a onPerfilClick
                                onPerfilClick = {
                                    navController.navigate("perfil/$usuarioId")
                                },
                                onNuevoCasoClick = {
                                    navController.navigate("nuevo_caso/$usuarioId")
                                }
                            )
                        }

                        composable(
                            route = "nuevo_caso/{usuarioId}",
                            arguments = listOf(navArgument("usuarioId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val usuarioId = backStackEntry.arguments?.getInt("usuarioId") ?: 0
                            val casoViewModel: CasoViewModel = viewModel(
                                factory = CasoViewModel.provideFactory(casoRepository)
                            )
                            NuevoCasoScreen(
                                viewModel = casoViewModel,
                                usuarioId = usuarioId,
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable(
                            route = "perfil/{usuarioId}",
                            arguments = listOf(navArgument("usuarioId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val usuarioId = backStackEntry.arguments?.getInt("usuarioId") ?: 0
                            val profileViewModel: ProfileViewModel =
                                viewModel(factory = profileViewModelFactory)

                            ProfileScreen(
                                viewModel = profileViewModel,
                                usuarioId = usuarioId,
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                onLogoutClick = {
                                    // Vuelve al login y limpia to|do el historial de navegación
                                    navController.navigate("login") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable(
                            route = "detalle_caso/{casoId}",
                            arguments = listOf(navArgument("casoId") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val casoId = backStackEntry.arguments?.getLong("casoId") ?: 0L

                            val detalleViewModel: DetalleCasoViewModel = viewModel(
                                key = "detalleCaso_$casoId",
                                factory = DetalleCasoViewModel.provideFactory(casoRepository, casoId)
                            )

                            DetalleCasoScreen(
                                viewModel = detalleViewModel,
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                onCasoCerrado = { id ->
                                    // Pasa a la pantalla de cierre y saca el detalle del historial
                                    navController.navigate("cierre_caso/$id") {
                                        popUpTo("detalle_caso/{casoId}") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable(
                            route = "cierre_caso/{casoId}",
                            arguments = listOf(navArgument("casoId") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val casoId = backStackEntry.arguments?.getLong("casoId") ?: 0L

                            CierreCasoScreen(
                                casoId = casoId,
                                onVolverAlInicio = {
                                    // Vuelve a la lista de casos (se recarga sola al aparecer)
                                    navController.popBackStack("lista_casos/{usuarioId}", false)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
