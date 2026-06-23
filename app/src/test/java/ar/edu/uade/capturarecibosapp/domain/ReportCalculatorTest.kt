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
    fun ReportCalculatorTest_ListasVacias_DevuelvenReporteVacio() {
        val result = ReportCalculator.calcular(emptyList(), emptyList())
        assertTrue(result.isEmpty())
    }

    @Test
    fun ReportCalculatorTest_UnTicket_ProduceUnReporteMensualCorrecto() {
        val result = ReportCalculator.calcular(listOf(ticket("2024-03-15", 1000f)), emptyList())
        assertEquals(1, result.size)
        assertEquals("MAR", result[0].month)
        assertEquals(1000f, result[0].amount, 0.01f)
    }

    @Test
    fun ReportCalculatorTest_DosTicketsMismoMes_SonAgregados() {
        val tickets = listOf(
            ticket("2024-06-01", 500f),
            ticket("2024-06-15", 300f)
        )
        val result = ReportCalculator.calcular(tickets, emptyList())
        assertEquals(1, result.size)
        assertEquals(800f, result[0].amount, 0.01f)
    }

    @Test
    fun ReportCalculatorTest_TicketsYGastosMismoMes_SonCombinados() {
        val result = ReportCalculator.calcular(
            listOf(ticket("2024-06-01", 400f)),
            listOf(expense("2024-06-10", 200.0))
        )
        assertEquals(1, result.size)
        assertEquals(600f, result[0].amount, 0.01f)
    }

    @Test
    fun ReportCalculatorTest_Reportes_EstanOrdenadosCronologicamente() {
        val tickets = listOf(
            ticket("2024-06-01", 300f),
            ticket("2024-01-01", 200f),
            ticket("2024-03-01", 100f)
        )
        val result = ReportCalculator.calcular(tickets, emptyList())
        assertEquals(listOf("ENE", "MAR", "JUN"), result.map { it.month })
    }

    @Test
    fun ReportCalculatorTest_EntradaConFechaInvalida_EsIgnorada() {
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
    fun ReportCalculatorTest_DoceMeses_EtiquetasCorrectas() {
        val expected = listOf("ENE", "FEB", "MAR", "ABR", "MAY", "JUN", "JUL", "AGO", "SEP", "OCT", "NOV", "DIC")
        val tickets = (1..12).map { month -> ticket("2024-%02d-01".format(month), 100f) }
        val result = ReportCalculator.calcular(tickets, emptyList())
        assertEquals(expected, result.map { it.month })
    }

    @Test
    fun ReportCalculatorTest_CostoPromedio_FormateadoCorrectamente() {
        val tickets = listOf(
            ticket("2024-06-01", 100f),
            ticket("2024-06-02", 300f)
        )
        val result = ReportCalculator.calcular(tickets, emptyList())
        assertEquals("\$200", result[0].averageCost)
    }

    @Test
    fun ReportCalculatorTest_MismoMesDiferentesAnios_ProduceDosReportesSeparados() {
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
    fun ReportCalculatorTest_DiaMasActivo_ReflejaDiaConMasEntradas() {
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
}