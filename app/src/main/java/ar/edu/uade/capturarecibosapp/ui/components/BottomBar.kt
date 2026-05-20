package ar.edu.uade.capturarecibosapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomBar(
    currentRoute: String = "inicio",
    onScanClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .background(Color.Transparent)
    ) {
        NavigationBar(
            containerColor = Color.White,
            contentColor = Color.Gray,
            tonalElevation = 8.dp,
            modifier = Modifier.align(Alignment.BottomCenter).height(70.dp)
        ) {
            BottomNavItem(
                icon = Icons.Default.Home,
                label = "Inicio",
                selected = currentRoute == "inicio",
                onClick = { /* Navegar a inicio */ }
            )
            BottomNavItem(
                icon = Icons.Default.AccountBalanceWallet,
                label = "Gastos",
                selected = currentRoute == "gastos",
                onClick = { /* Navegar a gastos */ }
            )
            
            Spacer(Modifier.weight(1f))

            BottomNavItem(
                icon = Icons.AutoMirrored.Filled.ReceiptLong,
                label = "Tickets",
                selected = currentRoute == "tickets",
                onClick = { /* Navegar a tickets */ }
            )
            BottomNavItem(
                icon = Icons.Default.Person,
                label = "Perfil",
                selected = currentRoute == "perfil",
                onClick = { /* Navegar a perfil */ }
            )
        }

        FloatingActionButton(
            onClick = onScanClick,
            shape = CircleShape,
            containerColor = Color(0xFF4F8CF6),
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(64.dp)
                .offset(y = (-10).dp),
            elevation = FloatingActionButtonDefaults.elevation(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PhotoCamera,
                contentDescription = "Escanear",
                modifier = Modifier.size(28.dp)
            )
        }
        
        Text(
            text = "ESCANEAR",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4F8CF6),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
        )
    }
}

@Composable
fun RowScope.BottomNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) Color(0xFF4F8CF6) else Color.Gray
            )
        },
        label = {
            Text(
                text = label,
                fontSize = 12.sp,
                color = if (selected) Color(0xFF4F8CF6) else Color.Gray
            )
        },
        colors = NavigationBarItemDefaults.colors(
            indicatorColor = Color.Transparent
        )
    )
}
