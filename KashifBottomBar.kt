package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.border
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkHeader
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldPrimary

enum class ShopScreen(val label: String, val icon: ImageVector) {
    DASHBOARD("Dashboard", Icons.Default.Dashboard),
    MOBILES("Mobiles", Icons.Default.PhoneAndroid),
    ACCESSORIES("Accessories", Icons.Default.Headset),
    REPAIRS("Repairs", Icons.Default.Build),
    SALES("Sales", Icons.Default.ShoppingCart),
    CUSTOMERS("Customers", Icons.Default.People),
    EXPENSES("Expenses", Icons.Default.AccountBalanceWallet),
    REPORTS("Reports", Icons.Default.Assessment),
    BACKUP("Backup", Icons.Default.Backup)
}

@Composable
fun KashifBottomBar(
    currentScreen: ShopScreen,
    onScreenSelected: (ShopScreen) -> Unit
) {
    Surface(
        color = DarkHeader,
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = DarkBorder)
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ShopScreen.entries.forEach { screen ->
                val isSelected = screen == currentScreen
                val containerColor = if (isSelected) GoldPrimary else DarkSurfaceVariant
                val contentColor = if (isSelected) Color.Black else Color.White

                Row(
                    modifier = Modifier
                        .height(42.dp)
                        .background(
                            color = containerColor,
                            shape = RoundedCornerShape(21.dp)
                        )
                        .then(
                            if (!isSelected) Modifier.border(
                                width = 1.dp,
                                color = DarkBorder,
                                shape = RoundedCornerShape(21.dp)
                            ) else Modifier
                        )
                        .clickable { onScreenSelected(screen) }
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.label,
                        tint = contentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = screen.label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = contentColor
                    )
                }
            }
        }
    }
}
