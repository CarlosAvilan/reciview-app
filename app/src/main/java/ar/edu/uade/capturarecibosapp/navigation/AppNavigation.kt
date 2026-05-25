package ar.edu.uade.capturarecibosapp.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
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
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                userName = "Juan",
                onCategoriesClick = { navController.navigate(Screen.Categories.route) },
                onManualClick = { navController.navigate(Screen.ManualExpense.route) },
                onProfileClick = { navController.navigate(Screen.Profile.route) }
            )
        }

        composable(Screen.Login.route) {
            val loginViewModel: LoginViewModel = viewModel()
            LoginScreen(
                viewModel = loginViewModel,
                onLoginRedirect = { navController.navigate(Screen.Welcome.route) },
                onRegisterClick = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(Screen.Categories.route) {
            ExpensesCategoriesScreen(
                onBackClick = { navController.popBackStack() },
                onEditCategoryClick = { category ->
                    navController.navigate(Screen.EditCategory.createRoute(category?.name ?: ""))
                }
            )
        }

        composable(Screen.EditCategory.route) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId")
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

        composable(Screen.MyExpenses.route){
            MyExpensesScreen(
                totalGastado = "$45.280,50",
                estadistica = "+ 25% vs enero",
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

        composable(Screen.Profile.route) {
            val profileViewModel: ProfileViewModel = viewModel()
            ProfileScreen(
                viewModel = profileViewModel,
                onPersonalInfoClick = { navController.navigate(Screen.PersonalInfo.route) },
                onCloseSessionClick = { navController.navigate(Screen.Login.route) }
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

        composable(Screen.ChangePassword.route){
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
                ConfirmationScreen (
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
