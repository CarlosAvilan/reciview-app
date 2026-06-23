package ar.edu.uade.capturarecibosapp.data.local.seeders

import ar.edu.uade.capturarecibosapp.data.model.User
import ar.edu.uade.capturarecibosapp.data.model.UserPreferences

class UserSeeder {
    companion object {
        const val MOCK_USER_ID = "user_mock_123"
    }

    fun provideInitialUser(): User = User(
        userId = MOCK_USER_ID,
        createdAt = "2026-06-13",
        email = "juan.perez@email.com",
        name = "Juan Pérez",
        phone = "+54 11 5555-5555",
        birth = "15 / 10 / 1990",
        country = "Argentina"
    )

    fun provideInitialPreferences(): UserPreferences = UserPreferences(
        notificationsOn = true,
        monthlyMax = 60000.0f,
        userId = MOCK_USER_ID
    )
}