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
import ar.edu.uade.capturarecibosapp.ui.screens.*
import ar.edu.uade.capturarecibosapp.ui.screens.success.*
import ar.edu.uade.capturarecibosapp.ui.viewmodel.*

@Composable
fun AppNavigation(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    startDestination: String,
    onScanClick: () -> Unit
) {
    // Escuchamos cambios en ticketDetectado para navegar a la pantalla de confirmación.
    LaunchedEffect(mainViewModel.ticketDetectado) {
        if (mainViewModel.ticketDetectado != null) {
            navController.navigate(Screen.Confirmation.route)
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
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
            )
        }

        composable(Screen.MyExpenses.route) {
            MyExpensesScreen(
                onCategoriesClick = { navController.navigate(Screen.Categories.route) },
                onViewAllClick = { navController.navigate(Screen.AllExpenses.route) },
                onScanClick = onScanClick
            )
        }

        composable(Screen.AllExpenses.route) {
            val myExpensesViewModel: MyExpensesViewModel = viewModel()
            AllExpensesScreen(
                viewModel = myExpensesViewModel,
                onBackClick = { navController.popBackStack() },
                onScanClick = onScanClick
            )
        }

        // --- CARGA MANUAL CON PANTALLA DE ÉXITO ---
        composable(Screen.ManualExpense.route) {
            val manualViewModel: ManualExpenseViewModel = viewModel()
            ManualExpenseScreen(
                viewModel = manualViewModel,
                onBackClick = { navController.popBackStack() },
                onSaveSuccess = {
                    navController.navigate(Screen.TicketRegistered.route) {
                        // Limpiamos la pantalla manual de la pila para no volver a ella con el botón atrás
                        popUpTo(Screen.Welcome.route) { inclusive = false }
                    }
                }
            )
        }

        // --- FLUJO RECUPERAR CONTRASEÑA ---
        composable(Screen.ForgotPassword.route) {
            val forgotPasswordViewModel: ForgotPasswordViewModel = viewModel()
            ForgotPasswordScreen(
                viewModel = forgotPasswordViewModel,
                onBackClick = { navController.popBackStack() },
                onCodeSent = { navController.navigate(Screen.VerifyCode.route) }
            )
        }
        composable(Screen.VerifyCode.route) {
            val backStackEntry = androidx.compose.runtime.remember(it) {
                navController.getBackStackEntry(Screen.ForgotPassword.route)
            }
            val forgotPasswordViewModel: ForgotPasswordViewModel = viewModel(backStackEntry)
            VerifyCodeScreen(viewModel = forgotPasswordViewModel, onBackClick = { navController.popBackStack() }, onCodeVerified = { navController.navigate(Screen.ResetPassword.route) })
        }
        composable(Screen.ResetPassword.route) {
            val backStackEntry = androidx.compose.runtime.remember(it) {
                navController.getBackStackEntry(Screen.VerifyCode.route)
            }
            val forgotPasswordViewModel: ForgotPasswordViewModel = viewModel(backStackEntry)
            ResetPasswordScreen(viewModel = forgotPasswordViewModel, onBackClick = { navController.popBackStack() }, onPasswordReset = { navController.navigate(Screen.PasswordSuccess.route) })
        }
        composable(Screen.PasswordSuccess.route) {
            PasswordSuccessScreen(onLoginClick = { navController.navigate(Screen.Login.route) { popUpTo(Screen.Login.route) { inclusive = true } } })
        }

        // --- CATEGORÍAS ---
        composable(Screen.Categories.route) {
            val categoriesViewModel: CategoriesViewModel = viewModel()
            ExpensesCategoriesScreen(
                viewModel = categoriesViewModel,
                onBackClick = { navController.popBackStack() },
                onEditCategoryClick = { category ->
                    val id = category?.name ?: return@ExpensesCategoriesScreen
                    navController.navigate(Screen.EditCategory.createRoute(id))
                },
                onCreateCategoryClick = {
                    navController.navigate(Screen.CreateCategory.route)
                }
            )
        }

        composable(Screen.CreateCategory.route) {
            val categoriesViewModel: CategoriesViewModel = viewModel()
            EditCategoriesScreen(
                userCategory = null,
                nameError = categoriesViewModel.nameError,
                budgetError = categoriesViewModel.budgetError,
                errorMessage = categoriesViewModel.errorMessage,
                onBackClick = { navController.popBackStack() },
                onSaveClick = { nombre, limite, icon ->
                    categoriesViewModel.saveCategory(nombre, limite, icon, null) { success ->
                        if (success) {
                            navController.navigate(Screen.CategorySuccess.route) {
                                popUpTo(Screen.Categories.route) { inclusive = false }
                            }
                        }
                    }
                }
            )
        }
        composable(
            route = Screen.EditCategory.route,
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
            val detailViewModel: CategoryDetailViewModel = viewModel()
            
            CategoryDetailScreen(
                categoryId = categoryId,
                viewModel = detailViewModel,
                onBackClick = { navController.popBackStack() },
                onScanClick = onScanClick,
                onSaveSuccess = {
                    navController.navigate(Screen.CategorySuccess.route) {
                        popUpTo(Screen.Categories.route) { inclusive = false }
                    }
                },
                onDeleteSuccess = {
                    navController.navigate(Screen.CategoryDeleteSuccess.route) {
                        popUpTo(Screen.Categories.route) { inclusive = false }
                    }
                }
            )
        }

        composable(Screen.CategorySuccess.route) {
            CategorySuccessScreen(
                onFinish = {
                    navController.popBackStack(Screen.Categories.route, inclusive = false)
                }
            )
        }

        composable(Screen.CategoryDeleteSuccess.route) {
            CategoryDeleteSuccessScreen(
                onFinish = {
                    navController.popBackStack(Screen.Categories.route, inclusive = false)
                }
            )
        }

        // --- REGISTRO Y TÉRMINOS ---
        composable(Screen.Register.route) {
            val registerViewModel: RegisterViewModel = viewModel()
            RegisterScreen(
                viewModel = registerViewModel,
                onBackClick = { navController.popBackStack() },
                onRegisterClick = { 
                    navController.navigate(Screen.RegisterSuccess.route) {
                        popUpTo(Screen.Login.route) { inclusive = false }
                    }
                },
                onTermsClick = { navController.navigate(Screen.TermsAndConditions.route) }
            )
        }

        composable(Screen.RegisterSuccess.route) {
            RegisterSuccessScreen(
                onAutoRedirect = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
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
            val profileViewModel: ProfileViewModel = viewModel()
            ProfileScreen(
                viewModel = profileViewModel, 
                onPersonalInfoClick = { navController.navigate(Screen.PersonalInfo.route) }, 
                onCloseSessionClick = { navController.navigate(Screen.Login.route) },
                onEditBudgetClick = { navController.navigate(Screen.EditBudget.route) }
            )
        }

        composable(Screen.EditBudget.route) {
            val profileViewModel: ProfileViewModel = viewModel()
            EditBudgetScreen(
                viewModel = profileViewModel,
                onBackClick = { navController.popBackStack() },
                onSaveSuccess = {
                    navController.navigate(Screen.BudgetSuccess.route) {
                        popUpTo(Screen.Profile.route) { inclusive = false }
                    }
                }
            )
        }

        composable(Screen.BudgetSuccess.route) {
            BudgetSuccessScreen(
                onFinish = {
                    navController.popBackStack(Screen.Profile.route, inclusive = false)
                }
            )
        }
        composable(Screen.Tickets.route) { TicketsScreen(viewModel = viewModel()) }
        composable(Screen.Reports.route) { ReportsScreen(onBackClick = { navController.popBackStack() }) }
        composable(Screen.Help.route) { HelpScreen(onBackClick = { navController.popBackStack() }) }

        composable(Screen.Confirmation.route) {
            val ticket = mainViewModel.ticketDetectado

            if (ticket != null) {
                ConfirmationScreen(
                    ticket = ticket,
                    onConfirm = {
                        navController.navigate(Screen.TicketRegistered.route) {
                            mainViewModel.confirmarYSubir(ticket)
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
            ChangePasswordScreen(viewModel = viewModel(), onBackClick = { navController.popBackStack() }, onSaveClick = { navController.navigate(Screen.PasswordSuccess.route) } )
        }
    }
}
