package com.duoc.securitysjapp

import android.content.Context
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
import com.duoc.securitysjapp.ui.CierreCasoScreen
import com.duoc.securitysjapp.ui.DetalleCasoScreen
import com.duoc.securitysjapp.ui.InicioScreen
import com.duoc.securitysjapp.ui.ListaCasosScreen
import com.duoc.securitysjapp.ui.LoginScreen
import com.duoc.securitysjapp.ui.MapaCasosScreen
import com.duoc.securitysjapp.ui.NuevoCasoScreen
import com.duoc.securitysjapp.ui.ProfileScreen
import com.duoc.securitysjapp.ui.RegistroScreen
import com.duoc.securitysjapp.ui.theme.SecuritySJAppTheme
import com.duoc.securitysjapp.viewModel.CasoViewModel
import com.duoc.securitysjapp.viewModel.DetalleCasoViewModel
import com.duoc.securitysjapp.viewModel.ListaCasosViewModel
import com.duoc.securitysjapp.viewModel.MapaCasosViewModel
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
        // Repositorios: reciben el DAO correspondiente
        val usuarioRepository = UsuarioRepository(database.usuarioDao())
        val casoRepository = CasoRepository(database.casoDao())
        // Factories para inyectar el repositorio en los ViewModels
        val usuarioViewModelFactory = UsuarioFormViewModelFactory(usuarioRepository)
        val profileViewModelFactory = ProfileViewModelFactory(usuarioRepository)

        // Gestión de persistencia de sesión con SharedPreferences
        val sharedPreferences = applicationContext.getSharedPreferences("security_prefs", Context.MODE_PRIVATE)
        val usuarioGuardadoId = sharedPreferences.getInt("usuario_id", -1)
        val startDestination = if (usuarioGuardadoId != -1) "inicio/$usuarioGuardadoId" else "login"

        setContent {
            SecuritySJAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White,
                ) {
                    val navController = rememberNavController()

                    // Pestañas de la barra inferior: cambia de pantalla sin apilar historial
                    fun irAPestana(ruta: String) {
                        navController.navigate(ruta) {
                            popUpTo("inicio/{usuarioId}")
                            launchSingleTop = true
                        }
                    }

                    NavHost(navController = navController, startDestination = startDestination) {

                        composable("login") {
                            val authViewModel: UsuarioFormViewModel =
                                viewModel(factory = usuarioViewModelFactory)
                            LoginScreen(
                                viewModel = authViewModel,
                                onLoginExitoso = { usuarioId ->
                                    // Guarda la sesión del usuario en SharedPreferences
                                    sharedPreferences.edit().putInt("usuario_id", usuarioId).apply()

                                    // Entra al menú de inicio con el id del usuario logueado
                                    navController.navigate("inicio/$usuarioId") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
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

                        // Menú principal
                        composable(
                            route = "inicio/{usuarioId}",
                            arguments = listOf(navArgument("usuarioId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val usuarioId = backStackEntry.arguments?.getInt("usuarioId") ?: 0
                            val profileViewModel: ProfileViewModel =
                                viewModel(factory = profileViewModelFactory)
                            InicioScreen(
                                viewModel = profileViewModel,
                                usuarioId = usuarioId,
                                onNuevoCasoClick = { navController.navigate("nuevo_caso/$usuarioId") },
                                onMisCasosClick = { irAPestana("mis_casos/$usuarioId") },
                                onTodosCasosClick = { navController.navigate("todos_casos/$usuarioId") },
                                onMapaClick = { navController.navigate("mapa_casos/$usuarioId") },
                                onPerfilClick = { irAPestana("perfil/$usuarioId") }
                            )
                        }

                        // Solo los casos del usuario logueado
                        composable(
                            route = "mis_casos/{usuarioId}",
                            arguments = listOf(navArgument("usuarioId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val usuarioId = backStackEntry.arguments?.getInt("usuarioId") ?: 0
                            val listaViewModel: ListaCasosViewModel = viewModel(
                                factory = ListaCasosViewModel.provideFactory(casoRepository, usuarioId, true)
                            )
                            ListaCasosScreen(
                                viewModel = listaViewModel,
                                titulo = "Mis casos",
                                onNavigateBack = { navController.popBackStack() },
                                onCasoClick = { caso ->
                                    navController.navigate("detalle_caso/${caso.id}/$usuarioId")
                                },
                                onInicioClick = {
                                    navController.popBackStack("inicio/{usuarioId}", false)
                                },
                                onPerfilClick = { irAPestana("perfil/$usuarioId") }
                            )
                        }

                        // Casos de todos los usuarios
                        composable(
                            route = "todos_casos/{usuarioId}",
                            arguments = listOf(navArgument("usuarioId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val usuarioId = backStackEntry.arguments?.getInt("usuarioId") ?: 0
                            val listaViewModel: ListaCasosViewModel = viewModel(
                                factory = ListaCasosViewModel.provideFactory(casoRepository, usuarioId, false)
                            )
                            ListaCasosScreen(
                                viewModel = listaViewModel,
                                titulo = "Todos los casos",
                                mostrarBarraInferior = false,
                                onNavigateBack = { navController.popBackStack() },
                                onCasoClick = { caso ->
                                    navController.navigate("detalle_caso/${caso.id}/$usuarioId")
                                }
                            )
                        }

                        // Mapa con un pin por cada caso que tiene ubicación GPS
                        composable(
                            route = "mapa_casos/{usuarioId}",
                            arguments = listOf(navArgument("usuarioId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val usuarioId = backStackEntry.arguments?.getInt("usuarioId") ?: 0
                            val mapaViewModel: MapaCasosViewModel = viewModel(
                                factory = MapaCasosViewModel.provideFactory(casoRepository)
                            )
                            MapaCasosScreen(
                                viewModel = mapaViewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onVerDetalle = { caso ->
                                    navController.navigate("detalle_caso/${caso.id}/$usuarioId")
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
                                onNavigateBack = { navController.popBackStack() }
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
                                onNavigateBack = { navController.popBackStack() },
                                onInicioClick = {
                                    navController.popBackStack("inicio/{usuarioId}", false)
                                },
                                onCasosClick = { irAPestana("mis_casos/$usuarioId") },
                                onLogoutClick = {
                                    // Borra la sesión guardada en SharedPreferences
                                    sharedPreferences.edit().clear().apply()

                                    // Vuelve al login y limpia completo el historial de navegación
                                    navController.navigate("login") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable(
                            route = "detalle_caso/{casoId}/{usuarioId}",
                            arguments = listOf(
                                navArgument("casoId") { type = NavType.LongType },
                                navArgument("usuarioId") { type = NavType.IntType }
                            )
                        ) { backStackEntry ->
                            val casoId = backStackEntry.arguments?.getLong("casoId") ?: 0L
                            val usuarioId = backStackEntry.arguments?.getInt("usuarioId") ?: 0

                            val detalleViewModel: DetalleCasoViewModel = viewModel(
                                key = "detalleCaso_$casoId",
                                factory = DetalleCasoViewModel.provideFactory(
                                    casoRepository, usuarioRepository, casoId, usuarioId
                                )
                            )

                            DetalleCasoScreen(
                                viewModel = detalleViewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onCasoCerrado = { id ->
                                    // Pasa a la pantalla de cierre y saca el detalle del historial
                                    navController.navigate("cierre_caso/$id") {
                                        popUpTo("detalle_caso/{casoId}/{usuarioId}") { inclusive = true }
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
                                    // Vuelve al menú de inicio
                                    navController.popBackStack("inicio/{usuarioId}", false)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
