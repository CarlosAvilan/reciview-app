package ar.edu.uade.capturarecibosapp.navigation

import androidx.compose.runtime.Composable
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
    val forgotPasswordViewModel: ForgotPasswordViewModel = viewModel()

    // Lista mock centralizada para asegurar consistencia en la navegación
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
                onCategoriesClick = { navController.navigate(Screen.Categories.route) },
                onProfileClick = { navController.navigate(Screen.Profile.route) },
                onManualClick = { navController.navigate(Screen.ManualExpense.route) },
                onReportsClick = { navController.navigate(Screen.Reports.route) },
                onHelpClick = { navController.navigate(Screen.Help.route) },
                onScanClick = startScan
            )
        }

        composable(Screen.MyExpenses.route) {
            MyExpensesScreen(
                onCategoriesClick = { navController.navigate(Screen.Categories.route) },
                onViewAllClick = { navController.navigate(Screen.Tickets.route) }
            )
        }

        composable(Screen.ManualExpense.route) {
            val manualViewModel: ManualExpenseViewModel = viewModel()
            ManualExpenseScreen(
                viewModel = manualViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        // --- FLUJO RECUPERAR CONTRASEÑA ---
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                viewModel = forgotPasswordViewModel,
                onBackClick = { navController.popBackStack() },
                onCodeSent = { navController.navigate(Screen.VerifyCode.route) }
            )
        }
        composable(Screen.VerifyCode.route) {
            VerifyCodeScreen(viewModel = forgotPasswordViewModel, onBackClick = { navController.popBackStack() }, onCodeVerified = { navController.navigate(Screen.ResetPassword.route) })
        }
        composable(Screen.ResetPassword.route) {
            ResetPasswordScreen(viewModel = forgotPasswordViewModel, onBackClick = { navController.popBackStack() }, onPasswordReset = { navController.navigate(Screen.PasswordSuccess.route) })
        }
        composable(Screen.PasswordSuccess.route) {
            PasswordSuccessScreen(onLoginClick = { navController.navigate(Screen.Login.route) { popUpTo(Screen.Login.route) { inclusive = true } } })
        }

        // --- CATEGORÍAS ---
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
                mockCategories.find { it.name == categoryId }
            }
            EditCategoriesScreen(category = categoryToEdit, onBackClick = { navController.popBackStack() }, onSaveClick = { _, _ -> navController.popBackStack() })
        }

        // --- REGISTRO Y TÉRMINOS ---
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
            val backStackEntry = remember(it) {
                navController.getBackStackEntry(Screen.Register.route)
            }
            val registerViewModel: RegisterViewModel = viewModel(backStackEntry)
            
            TermsAndConditionsScreen(
                viewModel = registerViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        // --- ÉXITO REGISTRO TICKET ---
        composable(Screen.TicketRegistered.route) {
            TicketRegisteredScreen(
                onHomeClick = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            )
        }

        // --- OTROS ---
        composable(Screen.Profile.route) {
            ProfileScreen(viewModel = viewModel(), onPersonalInfoClick = { navController.navigate(Screen.PersonalInfo.route) }, onCloseSessionClick = { navController.navigate(Screen.Login.route) })
        }
        composable(Screen.Tickets.route) { TicketsScreen(viewModel = viewModel()) }
        composable(Screen.Reports.route) { ReportsScreen(onBackClick = { navController.popBackStack() }) }
        composable(Screen.Help.route) { HelpScreen(onBackClick = { navController.popBackStack() }) }

        composable(Screen.Confirmation.route) {
            val ticket = mainViewModel.ticketDetectado
            if (ticket != null) {
                ConfirmationScreen(
                    ticket = ticket,
                    onConfirm = { ticketEditado ->
                        mainViewModel.confirmarYSubir(ticketEditado)
                        // Navegamos a la pantalla de éxito
                        navController.navigate(Screen.TicketRegistered.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = false }
                        }
                    },
                    onCancel = {
                        mainViewModel.cancelarCaptura()
                        navController.popBackStack()
                    }
                )
            }
        }
        
        composable(Screen.PersonalInfo.route) { 
            PersonalInfoScreen(viewModel = viewModel(), onBackClick = { navController.popBackStack() }, onChangePasswordClick = { navController.navigate(Screen.ChangePassword.route) }, onSaveClick = { navController.popBackStack() }, onDeleteAccountClick = { navController.navigate(Screen.Login.route) } )
        }
        composable(Screen.ChangePassword.route) { 
            ChangePasswordScreen(viewModel = viewModel(), onBackClick = { navController.popBackStack() }, onSaveClick = { navController.popBackStack() } )
        }
        composable(Screen.MyExpenses.route) { MyExpensesScreen(onCategoriesClick = { navController.navigate(Screen.Categories.route) }, onViewAllClick = { navController.navigate(Screen.Tickets.route) }) }
    }
}
