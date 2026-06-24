package ar.edu.uade.capturarecibosapp.events

sealed class MainNavigationEvent {
    data object NavigateToConfirmation : MainNavigationEvent()
    data object NavigateToTicketRegistered : MainNavigationEvent()
}

sealed class AuthNavigationEvent {
    data object NavigateToHome : AuthNavigationEvent()
    data object NavigateToRegisterSuccess : AuthNavigationEvent()
}

sealed class CategoryNavigationEvent {
    data object NavigateToSuccess : CategoryNavigationEvent()
    data object NavigateToCreatedSuccess : CategoryNavigationEvent()
    data object NavigateToDeleteSuccess : CategoryNavigationEvent()
}

sealed class ManualExpenseNavigationEvent {
    data object NavigateToSuccess : ManualExpenseNavigationEvent()
}

sealed class ForgotPasswordNavigationEvent {
    data object NavigateToVerifyCode : ForgotPasswordNavigationEvent()
    data object NavigateToResetPassword : ForgotPasswordNavigationEvent()
    data object NavigateToSuccess : ForgotPasswordNavigationEvent()
}

sealed class ProfileNavigationEvent {
    data object NavigateToProfile : ProfileNavigationEvent()
    data object NavigateToLogin : ProfileNavigationEvent()
    data object NavigateToBudgetSuccess : ProfileNavigationEvent()
}
