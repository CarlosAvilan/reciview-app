package ar.edu.uade.capturarecibosapp.navigation

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object Register : Screen("register")
    object ManualExpense : Screen("manual_expense")
    object MyExpenses: Screen("my_expenses")
    object Confirmation : Screen("confirmation")
    object Profile : Screen("profile")
    object PersonalInfo : Screen("personal_info")
    object ChangePassword : Screen("change_password")
    object Categories : Screen("categories")
    object Tickets : Screen("tickets")
    object Reports : Screen("reports")
    object Help : Screen("help")
    object EditCategory : Screen("edit_category/{categoryId}") {
        fun createRoute(categoryId: String) = "edit_category/$categoryId"
    }

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
