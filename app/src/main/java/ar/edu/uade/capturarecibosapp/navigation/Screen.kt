package ar.edu.uade.capturarecibosapp.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Tutorial : Screen("tutorial")
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object Register : Screen("register")
    object RegisterSuccess : Screen("register_success")
    object ManualExpense : Screen("manual_expense")
    object MyExpenses: Screen("my_expenses")
    object AllExpenses: Screen("all_expenses")
    object Confirmation : Screen("confirmation")
    object TicketRegistered : Screen("ticket_registered")
    object Profile : Screen("profile")
    object PersonalInfo : Screen("personal_info")
    object ChangePassword : Screen("change_password")
    object Categories : Screen("categories")
    object Tickets : Screen("tickets")
    object Reports : Screen("reports")
    object Help : Screen("help")
    object TermsAndConditions : Screen("terms_and_conditions")
    object EditBudget : Screen("edit_budget")
    object BudgetSuccess : Screen("budget_success")
    object EditCategory : Screen("edit_category/{categoryId}") {
        fun createRoute(categoryId: String) = "edit_category/$categoryId"
    }
    object CreateCategory : Screen("create_category")
    object CategorySuccess : Screen("category_success")
    object CategoryCreatedSuccess : Screen("category_created_success")
    object CategoryDeleteSuccess : Screen("category_delete_success")
    
    // Recupero de contraseña
    object ForgotPassword : Screen("forgot_password")
    object VerifyCode : Screen("verify_code")
    object ResetPassword : Screen("reset_password")
    object PasswordSuccess : Screen("password_success")

    // Pantallas que llevan BottomBar
    companion object {
        val bottomBarScreens = listOf(
            Welcome.route,
            MyExpenses.route,
            Categories.route,
            Profile.route,
            Tickets.route
        )
    }
}
