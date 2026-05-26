package ar.edu.uade.capturarecibosapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ar.edu.uade.capturarecibosapp.ui.components.BarItem
import ar.edu.uade.capturarecibosapp.ui.components.StatCard
import ar.edu.uade.capturarecibosapp.ui.components.TopBar
import ar.edu.uade.capturarecibosapp.ui.theme.ReciViewTheme
import ar.edu.uade.capturarecibosapp.ui.viewmodel.ReportsViewModel
import kotlinx.coroutines.launch

@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel = ReportsViewModel(),
    onBackClick: () -> Unit
) {
    // 1. Estado para el mensaje emergente (Snackbar)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopBar(
                title = "Reportes Mensuales",
                onBackClick = onBackClick,
                containerColor = MaterialTheme.colorScheme.surface
            )
        },
        // 2. Vinculamos el host del Snackbar al Scaffold
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Chart Card
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 24.dp)
                ) {
                    Text(
                        text = "Evolución 2026",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val maxAmount = viewModel.monthlyEvolution.maxOf { it.amount }
                        viewModel.monthlyEvolution.forEach { report ->
                            BarItem(
                                report = report,
                                isSelected = viewModel.selectedReport == report,
                                heightFactor = report.amount / maxAmount,
                                onClick = { viewModel.onReportSelected(report) }
                            )
                        }
                    }
                }
            }

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    title = "Gasto Promedio",
                    value = viewModel.selectedReport.averageCost,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Día más activo",
                    value = viewModel.selectedReport.mostActiveDay,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Download Button
            Button(
                onClick = { 
                    // 3. Mostramos el mensaje al hacer click
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Se ha descargado el archivo",
                            duration = SnackbarDuration.Short
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4B89F1)
                )
            ) {
                Text(
                    text = "Descargar PDF de ${viewModel.selectedReport.month}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReportsScreenPreview() {
    ReciViewTheme {
        ReportsScreen(onBackClick = {})
    }
}
