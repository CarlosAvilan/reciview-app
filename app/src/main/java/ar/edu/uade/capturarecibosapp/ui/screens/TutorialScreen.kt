package ar.edu.uade.capturarecibosapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ar.edu.uade.capturarecibosapp.R
import ar.edu.uade.capturarecibosapp.ui.components.Button
import kotlinx.coroutines.launch

data class TutorialPageData(
    val title: String,
    val description: String,
    val imageRes: Int
)

@Composable
fun TutorialScreen(onFinish: () -> Unit) {
    val pages = listOf(
        TutorialPageData(
            title = "Captura Instantánea",
            description = "Digitalizá tus tickets físicos en segundos usando la cámara de tu celular.",
            imageRes = R.drawable.tutorial1
        ),
        TutorialPageData(
            title = "Extracción Inteligente",
            description = "Nuestro sistema procesa el texto automáticamente para identificar el comercio y el monto total sin que tengas que tipear de más.",
            imageRes = R.drawable.tutorial2
        ),
        TutorialPageData(
            title = "Control de Gastos",
            description = "Visualizá tus consumos mediante gráficos y mantené tus finanzas al día, incluso si te quedás sin conexión a internet.",
            imageRes = R.drawable.tutorial3
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Botón Saltar en la parte superior
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                TextButton(onClick = onFinish) {
                    Text(text = "Saltar", color = Color.Gray)
                }
            }

            // Pager central
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { pageIndex ->
                TutorialPageContent(pages[pageIndex])
            }

            // Parte inferior: Indicadores y Botón
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                // Dots (Indicadores de página)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(pages.size) { index ->
                        val isSelected = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) Color(0xFF4F8CF6) else Color(0xFFE9ECEF))
                        )
                    }
                }

                // Botón principal dinámico
                val isLastPage = pagerState.currentPage == pages.size - 1
                Button(
                    text = if (isLastPage) "Comenzar" else "Siguiente",
                    onClick = {
                        if (isLastPage) {
                            onFinish()
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun TutorialPageContent(page: TutorialPageData) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Ilustración en círculo
        Box(
            modifier = Modifier
                .size(240.dp)
                .clip(CircleShape)
                .background(Color(0xFFE9F2FF)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = page.imageRes),
                contentDescription = null,
                modifier = Modifier.size(160.dp)
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}
