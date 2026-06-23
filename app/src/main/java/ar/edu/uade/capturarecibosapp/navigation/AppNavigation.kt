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
import ar.edu.uade.capturarecibosapp.events.*
import ar.edu.uade.capturarecibosapp.ui.screens.*
import ar.edu.uade.capturarecibosapp.ui.screens.success.*
import ar.edu.uade.capturarecibosapp.ui.viewmodel.*
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AppNavigation(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    startDestination: String,
    onScanClick: () -> Unit
) {
    // Eventos globales (OCR)
    LaunchedEffect(mainViewModel.navigationEvents, navController) {
        mainViewModel.navigationEvents.collectLatest { event ->
            when (event) {
                is MainNavigationEvent.NavigateToConfirmation -> {
                    navController.navigate(Screen.Confirmation.route)
                }
                is MainNavigationEvent.NavigateToTicketRegistered -> {
                    navController.navigate(Screen.TicketRegistered.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = false }
                    }
                }
            }
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
            
            LaunchedEffect(loginViewModel.navigationEvents, navController) {
                loginViewModel.navigationEvents.collectLatest { event ->
                    when (event) {
                        is AuthNavigationEvent.NavigateToHome -> {
                            navController.navigate(Screen.Welcome.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                        is AuthNavigationEvent.NavigateToRegisterSuccess -> {}
                    }
                }
            }

            LoginScreen(
                viewModel = loginViewModel,
                onRegisterClick = { navController.navigate(Screen.Register.route) },
                onForgotPasswordClick = { navController.navigate(Screen.ForgotPassword.route) }
            )
        }

        composable(Screen.Welcome.route) {
            val welcomeViewModel: WelcomeViewModel = viewModel()
            WelcomeScreen(
                viewModel = welcomeViewModel,
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
            
            LaunchedEffect(manualViewModel.navigationEvents, navController) {
                manualViewModel.navigationEvents.collectLatest { event ->
                    when (event) {
                        is ManualExpenseNavigationEvent.NavigateToSuccess -> {
                            navController.navigate(Screen.TicketRegistered.route) {
                                popUpTo(Screen.Welcome.route) { inclusive = false }
                            }
                        }
                    }
                }
            }

            ManualExpenseScreen(
                viewModel = manualViewModel,
                onBackClick = { navController.popBackStack() },
            )
        }

        // --- FLUJO RECUPERAR CONTRASEÑA ---
        composable(Screen.ForgotPassword.route) {
            val forgotPasswordViewModel: ForgotPasswordViewModel = viewModel()
            
            LaunchedEffect(forgotPasswordViewModel.navigationEvents, navController) {
                forgotPasswordViewModel.navigationEvents.collectLatest { event ->
                    when (event) {
                        is ForgotPasswordNavigationEvent.NavigateToVerifyCode -> {
                            navController.navigate(Screen.VerifyCode.route)
                        }
                        else -> {}
                    }
                }
            }

            ForgotPasswordScreen(
                viewModel = forgotPasswordViewModel,
                onBackClick = { navController.popBackStack() },
                onCodeSent = { }
            )
        }

        composable(Screen.VerifyCode.route) {
            val backStackEntry = remember(it) {
                navController.getBackStackEntry(Screen.ForgotPassword.route)
            }
            val forgotPasswordViewModel: ForgotPasswordViewModel = viewModel(backStackEntry)
            
            LaunchedEffect(forgotPasswordViewModel.navigationEvents, navController) {
                forgotPasswordViewModel.navigationEvents.collectLatest { event ->
                    when (event) {
                        is ForgotPasswordNavigationEvent.NavigateToResetPassword -> {
                            navController.navigate(Screen.ResetPassword.route)
                        }
                        else -> {}
                    }
                }
            }

            ForgotPasswordEmailSent(
                onLoginClick = { navController.navigate(Screen.Login.route) }
            )
        }

        composable(Screen.ResetPassword.route) {
            val backStackEntry = remember(it) {
                navController.getBackStackEntry(Screen.ForgotPassword.route)
            }
            val forgotPasswordViewModel: ForgotPasswordViewModel = viewModel(backStackEntry)

            LaunchedEffect(forgotPasswordViewModel.navigationEvents, navController) {
                forgotPasswordViewModel.navigationEvents.collectLatest { event ->
                    when (event) {
                        is ForgotPasswordNavigationEvent.NavigateToSuccess -> {
                            navController.navigate(Screen.PasswordSuccess.route)
                        }
                        else -> {}
                    }
                }
            }

            ResetPasswordScreen(
                viewModel = forgotPasswordViewModel, 
                onBackClick = { navController.popBackStack() }, 
                onPasswordReset = { }
            )
        }

        composable(Screen.PasswordSuccess.route) {
            PasswordSuccessScreen(onLoginClick = { 
                navController.navigate(Screen.Login.route) { 
                    popUpTo(Screen.Login.route) { inclusive = true } 
                } 
            })
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
            
            LaunchedEffect(categoriesViewModel.navigationEvents, navController) {
                categoriesViewModel.navigationEvents.collectLatest { event ->
                    when (event) {
                        is CategoryNavigationEvent.NavigateToSuccess -> {
                            navController.navigate(Screen.CategorySuccess.route) {
                                popUpTo(Screen.Categories.route) { inclusive = false }
                            }
                        }
                        is CategoryNavigationEvent.NavigateToDeleteSuccess -> {}
                    }
                }
            }

            EditCategoriesScreen(
                viewModel = categoriesViewModel,
                userCategory = null,
                onBackClick = { navController.popBackStack() },
            )
        }

        composable(
            route = Screen.EditCategory.route,
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
            val detailViewModel: CategoryDetailViewModel = viewModel()
            
            LaunchedEffect(detailViewModel.navigationEvents, navController) {
                detailViewModel.navigationEvents.collectLatest { event ->
                    when (event) {
                        is CategoryNavigationEvent.NavigateToSuccess -> {
                            navController.navigate(Screen.CategorySuccess.route) {
                                popUpTo(Screen.Categories.route) { inclusive = false }
                            }
                        }
                        is CategoryNavigationEvent.NavigateToDeleteSuccess -> {
                            navController.navigate(Screen.CategoryDeleteSuccess.route) {
                                popUpTo(Screen.Categories.route) { inclusive = false }
                            }
                        }
                    }
                }
            }
            
            CategoryDetailScreen(
                categoryId = categoryId,
                viewModel = detailViewModel,
                onBackClick = { navController.popBackStack() },
                onScanClick = onScanClick,
            )
        }

        composable(Screen.CategorySuccess.route) {
            CategorySuccessScreen(
                onFinish = {
                    navController.popBackStack(Screen.Categories.route, inclusive = false)
                }
            )
        }

        // --- REGISTRO Y TÉRMINOS ---
        composable(Screen.Register.route) {
            val registerViewModel: RegisterViewModel = viewModel()
            
            LaunchedEffect(registerViewModel.navigationEvents, navController) {
                registerViewModel.navigationEvents.collectLatest { event ->
                    when (event) {
                        is AuthNavigationEvent.NavigateToRegisterSuccess -> {
                            navController.navigate(Screen.RegisterSuccess.route) {
                                popUpTo(Screen.Login.route) { inclusive = false }
                            }
                        }
                        is AuthNavigationEvent.NavigateToHome -> {}
                    }
                }
            }

            RegisterScreen(
                viewModel = registerViewModel,
                onBackClick = { navController.popBackStack() },
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
            
            LaunchedEffect(profileViewModel.navigationEvents, navController) {
                profileViewModel.navigationEvents.collectLatest { event ->
                    when (event) {
                        is ProfileNavigationEvent.NavigateToLogin -> {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                        else -> {}
                    }
                }
            }

            ProfileScreen(
                viewModel = profileViewModel, 
                onPersonalInfoClick = { navController.navigate(Screen.PersonalInfo.route) }, 
                onEditBudgetClick = { navController.navigate(Screen.EditBudget.route) }
            )
        }

        composable(Screen.EditBudget.route) {
            val profileViewModel: ProfileViewModel = viewModel()
            
            LaunchedEffect(profileViewModel.navigationEvents, navController) {
                profileViewModel.navigationEvents.collectLatest { event ->
                    when (event) {
                        is ProfileNavigationEvent.NavigateToBudgetSuccess -> {
                            navController.navigate(Screen.BudgetSuccess.route) {
                                popUpTo(Screen.Profile.route) { inclusive = false }
                            }
                        }
                        else -> {}
                    }
                }
            }

            EditBudgetScreen(
                viewModel = profileViewModel,
                onBackClick = { navController.popBackStack() },
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
            val manualViewModel : ManualExpenseViewModel = viewModel()
            
            LaunchedEffect(manualViewModel.navigationEvents) {
                manualViewModel.navigationEvents.collectLatest { event ->
                    when (event) {
                        is ManualExpenseNavigationEvent.NavigateToSuccess -> {
                            mainViewModel.cancelarCaptura()
                            navController.navigate(Screen.TicketRegistered.route) {
                                popUpTo(Screen.Welcome.route) { inclusive = false }
                            }
                        }
                    }
                }
            }

            if (ticket != null) {
                ConfirmationScreen(
                    ticket = ticket,
                    viewModel = manualViewModel,
                    onCancel = {
                        mainViewModel.cancelarCaptura()
                        navController.popBackStack()
                    }
                )
            }
        }
        
        composable(Screen.PersonalInfo.route) { 
            val personalInfoViewModel: PersonalInfoViewModel = viewModel()
            
            LaunchedEffect(personalInfoViewModel.navigationEvents, navController) {
                personalInfoViewModel.navigationEvents.collectLatest { event ->
                    when (event) {
                        is ProfileNavigationEvent.NavigateToProfile -> {
                            navController.popBackStack()
                        }
                        is ProfileNavigationEvent.NavigateToLogin -> {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                        else -> {}
                    }
                }
            }

            PersonalInfoScreen(
                viewModel = personalInfoViewModel, 
                onBackClick = { navController.popBackStack() }, 
                onChangePasswordClick = { navController.navigate(Screen.ChangePassword.route) }, 
            )
        }

        composable(Screen.ChangePassword.route) { 
            val changePasswordViewModel: ChangePasswordViewModel = viewModel()
            
            LaunchedEffect(changePasswordViewModel.navigationEvents, navController) {
                changePasswordViewModel.navigationEvents.collectLatest { event ->
                    when (event) {
                        is ProfileNavigationEvent.NavigateToLogin -> {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                        is ProfileNavigationEvent.NavigateToBudgetSuccess -> {
                            navController.navigate(Screen.PasswordSuccess.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                        else -> {}
                    }
                }
            }

            ChangePasswordScreen(
                viewModel = changePasswordViewModel, 
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
