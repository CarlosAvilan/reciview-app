package ar.edu.uade.capturarecibosapp.data.local.seeders

import ar.edu.uade.capturarecibosapp.data.model.ExpenseItem

class ExpenseSeeder {
    fun provideInitialExpenses(): List<ExpenseItem> = listOf(
        ExpenseItem(
            userId = UserSeeder.MOCK_USER_ID,
            photoUrl = 0,
            title = "Supermercado Coto",
            date = "2026-06-01",
            category = "Alimentos",
            amount = 13250.0
        ),
        ExpenseItem(
            userId = UserSeeder.MOCK_USER_ID,
            photoUrl = 0,
            title = "Combustible YPF",
            date = "2026-06-05",
            category = "Transporte",
            amount = 8450.0
        ),
        ExpenseItem(
            userId = UserSeeder.MOCK_USER_ID,
            photoUrl = 0,
            title = "Farmacia",
            date = "2026-06-08",
            category = "Salud",
            amount = 3240.0
        ),
        ExpenseItem(
            userId = UserSeeder.MOCK_USER_ID,
            photoUrl = 0,
            title = "Cena en restaurante",
            date = "2026-06-10",
            category = "Entretenimiento",
            amount = 7600.0
        ),
        ExpenseItem(
            userId = UserSeeder.MOCK_USER_ID,
            photoUrl = 0,
            title = "Café Italiano",
            date = "2026-06-12",
            category = "Gastos personales",
            amount = 1120.0
        )
    )
}
