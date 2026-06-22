package ar.edu.uade.capturarecibosapp.domain

import ar.edu.uade.capturarecibosapp.data.model.ExpenseItem
import ar.edu.uade.capturarecibosapp.data.model.Ticket
import org.junit.Assert.*
import org.junit.Test

class ReportCalculatorTest {

    private fun ticket(date: String, amount: Float) = Ticket(
        createdAt = date,
        userId = "u1",
        categoryId = null,
        establishment = "Test",
        amount = amount,
        photoUrl = null
    )

    private fun expense(date: String, amount: Double) = ExpenseItem(
        photoUrl = 0,
        userId = "u1",
        title = "Test",
        date = date,
        category = "Varios",
        amount = amount
    )

    @Test
    fun `empty lists return empty report`() {
        val result = ReportCalculator.calcular(emptyList(), emptyList())
        assertTrue(result.isEmpty())
    }

    @Test
    fun `single ticket produces one monthly report with correct values`() {
        val result = ReportCalculator.calcular(listOf(ticket("2024-03-15", 1000f)), emptyList())
        assertEquals(1, result.size)
        assertEquals("MAR", result[0].month)
        assertEquals(1000f, result[0].amount, 0.01f)
    }

    @Test
    fun `two tickets in same month are aggregated`() {
        val tickets = listOf(
            ticket("2024-06-01", 500f),
            ticket("2024-06-15", 300f)
        )
        val result = ReportCalculator.calcular(tickets, emptyList())
        assertEquals(1, result.size)
        assertEquals(800f, result[0].amount, 0.01f)
    }

    @Test
    fun `tickets and expenses in same month are combined`() {
        val result = ReportCalculator.calcular(
            listOf(ticket("2024-06-01", 400f)),
            listOf(expense("2024-06-10", 200.0))
        )
        assertEquals(1, result.size)
        assertEquals(600f, result[0].amount, 0.01f)
    }

    @Test
    fun `reports are sorted chronologically`() {
        val tickets = listOf(
            ticket("2024-06-01", 300f),
            ticket("2024-01-01", 200f),
            ticket("2024-03-01", 100f)
        )
        val result = ReportCalculator.calcular(tickets, emptyList())
        assertEquals(listOf("ENE", "MAR", "JUN"), result.map { it.month })
    }

    @Test
    fun `entry with invalid date is ignored`() {
        val result = ReportCalculator.calcular(
            listOf(
                ticket("fecha-invalida", 1000f),
                ticket("2024-05-10", 500f)
            ),
            emptyList()
        )
        assertEquals(1, result.size)
        assertEquals("MAY", result[0].month)
    }

    @Test
    fun `all twelve month labels are correct`() {
        val expected = listOf("ENE", "FEB", "MAR", "ABR", "MAY", "JUN", "JUL", "AGO", "SEP", "OCT", "NOV", "DIC")
        val tickets = (1..12).map { month -> ticket("2024-%02d-01".format(month), 100f) }
        val result = ReportCalculator.calcular(tickets, emptyList())
        assertEquals(expected, result.map { it.month })
    }

    @Test
    fun `average cost is formatted correctly`() {
        val tickets = listOf(
            ticket("2024-06-01", 100f),
            ticket("2024-06-02", 300f)
        )
        val result = ReportCalculator.calcular(tickets, emptyList())
        assertEquals("\$200", result[0].averageCost)
    }

    @Test
    fun `same month in different years produces two separate reports`() {
        val result = ReportCalculator.calcular(
            listOf(
                ticket("2023-06-01", 100f),
                ticket("2024-06-01", 200f)
            ),
            emptyList()
        )
        assertEquals(2, result.size)
    }

    @Test
    fun `most active day reflects the day with most entries`() {
        // 2024-06-03 is Monday, 2024-06-04 is Tuesday
        val result = ReportCalculator.calcular(
            listOf(
                ticket("2024-06-03", 100f),
                ticket("2024-06-03", 200f),
                ticket("2024-06-04", 150f)
            ),
            emptyList()
        )
        assertEquals("Lunes", result[0].mostActiveDay)
    }

    @Test
    fun `only expense items without tickets produce a valid report`() {
        val result = ReportCalculator.calcular(
            emptyList(),
            listOf(expense("2024-09-20", 750.0))
        )
        assertEquals(1, result.size)
        assertEquals("SEP", result[0].month)
        assertEquals(750f, result[0].amount, 0.01f)
    }
}