package ar.edu.uade.capturarecibosapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ar.edu.uade.capturarecibosapp.ui.components.CategoryItem
import ar.edu.uade.capturarecibosapp.ui.screens.*
import ar.edu.uade.capturarecibosapp.ui.viewmodel.*

@Composable
fun AppNavigation(
    navController: NavHostController,
    startScan: () -> Unit,
    mainViewModel: MainViewModel
) {
    // Lógica de navegación centralizada en el componente de navegación (SRP)
    LaunchedEffect(mainViewModel.ticketDetectado) {
        if (mainViewModel.ticketDetectado != null) {
            navController.navigate(Screen.Confirmation.route)
        }
    }

    val mockCategories = listOf(
        CategoryItem("🍔", "Comida y Bebida", 18500.0, 25000.0),
        CategoryItem("🚗", "Transporte", 12200.0, 15000.0),
        CategoryItem("💡", "Servicios y Hogar", 9800.0, 8000.0)
    )

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateNext = {
                    navController.navigate(Screen.Tutorial.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Tutorial.route) {
            TutorialScreen(
                onFinish = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Tutorial.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            val loginViewModel: LoginViewModel = viewModel()
            LoginScreen(
                viewModel = loginViewModel,
                onLoginRedirect = { navController.navigate(Screen.Welcome.route) },
                onRegisterClick = { navController.navigate(Screen.Register.route) },
                onForgotPasswordClick = { navController.navigate(Screen.ForgotPassword.route) }
            )
        }

        composable(Screen.Welcome.route) {
            WelcomeScreen(
                userName = "Juan",
                onCategoriesClick = { navController.navigate(Screen.Categories.route) },
                onProfileClick = { navController.navigate(Screen.Profile.route) },
                onManualClick = { navController.navigate(Screen.ManualExpense.route) },
                onReportsClick = { navController.navigate(Screen.Reports.route) },
                onHelpClick = { navController.navigate(Screen.Help.route) },
                onScanClick = startScan
            )
        }

        composable(Screen.Categories.route) {
            ExpensesCategoriesScreen(
                onBackClick = { navController.popBackStack() },
                onEditCategoryClick = { category ->
                    val id = category?.name ?: "new"
                    navController.navigate(Screen.EditCategory.createRoute(id))
                }
            )
        }

        composable(
            route = Screen.EditCategory.route,
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId")
            val categoryToEdit = if (categoryId == "new" || categoryId == null) null else {
                mockCategories.find { it.name.equals(categoryId, ignoreCase = true) }
            }

            EditCategoriesScreen(
                category = categoryToEdit,
                onBackClick = { navController.popBackStack() },
                onSaveClick = { _, _ -> navController.popBackStack() }
            )
        }

        composable(Screen.Register.route) {
            val registerViewModel: RegisterViewModel = viewModel()
            RegisterScreen(
                viewModel = registerViewModel,
                onBackClick = { navController.popBackStack() },
                onRegisterClick = { navController.navigate(Screen.Welcome.route) },
                onTermsClick = { navController.navigate(Screen.TermsAndConditions.route) }
            )
        }

        composable(Screen.TermsAndConditions.route) {
            val backStackEntry = remember(it) { navController.getBackStackEntry(Screen.Register.route) }
            val registerViewModel: RegisterViewModel = viewModel(backStackEntry)
            TermsAndConditionsScreen(viewModel = registerViewModel, onBackClick = { navController.popBackStack() })
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                viewModel = viewModel(), 
                onPersonalInfoClick = { navController.navigate(Screen.PersonalInfo.route) }, 
                onCloseSessionClick = { navController.navigate(Screen.Login.route) }
            )
        }
        
        composable(Screen.Tickets.route) { TicketsScreen(viewModel = viewModel()) }

        composable(Screen.Confirmation.route) {
            val ticket = mainViewModel.ticketDetectado
            if (ticket != null) {
                ConfirmationScreen(
                    ticket = ticket,
                    onConfirm = { mainViewModel.confirmarYSubir(it); navController.popBackStack() },
                    onCancel = { mainViewModel.cancelarCaptura(); navController.popBackStack() }
                )
            }
        }
    }
}
