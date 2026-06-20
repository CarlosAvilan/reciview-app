package ar.edu.uade.capturarecibosapp.events

sealed class MainNavigationEvent {
    data object NavigateToConfirmation : MainNavigationEvent()
}