package ar.edu.uade.capturarecibosapp.domain

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import ar.edu.uade.capturarecibosapp.data.model.MonthlyReport
import java.io.File
import java.io.FileOutputStream

object ReportPdfGenerator {

    fun generate(context: Context, report: MonthlyReport): File {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 en puntos
        val page = document.startPage(pageInfo)

        drawPage(page.canvas, report)

        document.finishPage(page)

        val file = File(context.cacheDir, "reporte_${report.month}_2026.pdf")
        FileOutputStream(file).use { document.writeTo(it) }
        document.close()

        return file
    }

    private fun drawPage(canvas: Canvas, report: MonthlyReport) {
        val primaryColor = Color.parseColor("#6750A4")

        val titlePaint = Paint().apply {
            color = Color.WHITE
            textSize = 28f
            isFakeBoldText = true
            isAntiAlias = true
        }
        val headerBgPaint = Paint().apply {
            color = primaryColor
            style = Paint.Style.FILL
        }
        val labelPaint = Paint().apply {
            color = Color.GRAY
            textSize = 14f
            isAntiAlias = true
        }
        val valuePaint = Paint().apply {
            color = Color.BLACK
            textSize = 22f
            isFakeBoldText = true
            isAntiAlias = true
        }
        val dividerPaint = Paint().apply {
            color = Color.LTGRAY
            strokeWidth = 1f
        }

        // Encabezado con fondo de color
        canvas.drawRect(0f, 0f, 595f, 120f, headerBgPaint)
        canvas.drawText("ReciView", 40f, 60f, titlePaint)
        canvas.drawText("Reporte Mensual · ${report.month} 2026", 40f, 95f, titlePaint.apply { textSize = 16f; isFakeBoldText = false })

        // Datos del reporte
        var y = 180f
        val leftMargin = 40f

        fun drawRow(label: String, value: String) {
            canvas.drawText(label, leftMargin, y, labelPaint)
            y += 28f
            canvas.drawText(value, leftMargin, y, valuePaint)
            y += 20f
            canvas.drawLine(leftMargin, y, 555f, y, dividerPaint)
            y += 36f
        }

        drawRow("Total gastado", report.amount.let { "$${formatMonto(it.toDouble())}" })
        drawRow("Gasto promedio por transacción", report.averageCost)
        drawRow("Día más activo", report.mostActiveDay)

        // Pie
        val footerPaint = Paint().apply {
            color = Color.GRAY
            textSize = 11f
            isAntiAlias = true
        }
        canvas.drawText("Generado por ReciView · ${report.month} 2026", leftMargin, 800f, footerPaint)
    }

    private fun formatMonto(amount: Double): String =
        String.format(java.util.Locale.US, "%,.0f", amount).replace(",", ".")
}