package ar.edu.uade.capturarecibosapp.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ar.edu.uade.capturarecibosapp.ui.screens.*
import ar.edu.uade.capturarecibosapp.ui.viewmodel.*

@Composable
fun AppNavigation(
    navController: NavHostController,
    startScan: () -> Unit,
    mainViewModel: MainViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route
    ) {
        composable(Screen.Login.route) {
            val loginViewModel: LoginViewModel = viewModel()
            LoginScreen(
                viewModel = loginViewModel,
                onLoginRedirect = { navController.navigate(Screen.Welcome.route) },
                onRegisterClick = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(Screen.Welcome.route) {
            WelcomeScreen(
                userName = "Juan",
                onCategoriesClick = { navController.navigate(Screen.Categories.route) },
                onProfileClick = { navController.navigate(Screen.Profile.route) },
                onManualClick = { navController.navigate(Screen.ManualExpense.route) },
                onReportsClick = { navController.navigate(Screen.Reports.route) },
                onHelpClick = { navController.navigate(Screen.Help.route) }
            )
        }

        composable(Screen.Reports.route){
            ReportsScreen(onBackClick = { navController.popBackStack() },)
        }

        composable(Screen.Help.route){
            HelpScreen(onBackClick = { navController.popBackStack() })
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

        composable(Screen.EditCategory.route) {
            EditCategoriesScreen(
                category = null,
                onBackClick = { navController.popBackStack() },
                onSaveClick = { _, _ -> navController.popBackStack() }
            )
        }

        composable(Screen.Register.route) {
            val registerViewModel: RegisterViewModel = viewModel()
            RegisterScreen(
                viewModel = registerViewModel,
                onBackClick = { navController.popBackStack() },
                onRegisterClick = { navController.navigate(Screen.Welcome.route) }
            )
        }

        composable(Screen.MyExpenses.route) {
            MyExpensesScreen(
                onCategoriesClick = { navController.navigate(Screen.Categories.route) }
            )
        }

        composable(Screen.ManualExpense.route) {
            val manualViewModel: ManualExpenseViewModel = viewModel()
            ManualExpenseScreen(
                viewModel = manualViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Tickets.route) {
            val ticketsViewModel: TicketsViewModel = viewModel()
            TicketsScreen(
                viewModel = ticketsViewModel,
            )
        }

        composable(Screen.Profile.route) {
            val profileViewModel: ProfileViewModel = viewModel()
            ProfileScreen(
                viewModel = profileViewModel,
                onPersonalInfoClick = { navController.navigate(Screen.PersonalInfo.route) },
                onCloseSessionClick = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.PersonalInfo.route) {
            val personalInfoViewModel: PersonalInfoViewModel = viewModel()
            PersonalInfoScreen(
                viewModel = personalInfoViewModel,
                onBackClick = { navController.popBackStack() },
                onChangePasswordClick = { navController.navigate(Screen.ChangePassword.route) },
                onSaveClick = { navController.navigate(Screen.Profile.route) },
                onDeleteAccountClick = { navController.navigate(Screen.Login.route) }
            )
        }

        composable(Screen.ChangePassword.route) {
            val changePasswordViewModel: ChangePasswordViewModel = viewModel()
            ChangePasswordScreen(
                viewModel = changePasswordViewModel,
                onBackClick = { navController.popBackStack() },
                onSaveClick = { navController.navigate(Screen.PersonalInfo.route) }
            )
        }

        composable(Screen.Confirmation.route) {
            val ticket = mainViewModel.ticketDetectado
            if (ticket != null) {
                ConfirmationScreen(
                    ticket = ticket,
                    onConfirm = { ticketEditado ->
                        mainViewModel.confirmarYSubir(ticketEditado)
                        navController.popBackStack()
                    },
                    onCancel = {
                        mainViewModel.cancelarCaptura()
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
