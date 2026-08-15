package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ShopScreen
import com.example.ui.components.StatCard
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.ShopViewModel

@Composable
fun DashboardScreen(
    viewModel: ShopViewModel,
    onNavigate: (ShopScreen) -> Unit
) {
    val mobiles by viewModel.mobiles.collectAsState()
    val accessories by viewModel.accessories.collectAsState()
    val repairJobs by viewModel.repairJobs.collectAsState()
    val invoices by viewModel.invoices.collectAsState()
    val expenses by viewModel.expenses.collectAsState()

    val todayDate = ShopViewModel.getTodayDateString()

    // Calculations
    val totalMobilesQty = mobiles.sumOf { it.quantity }
    val totalMobileStockValue = mobiles.sumOf { it.purchasePrice * it.quantity }

    val totalAccessoriesQty = accessories.sumOf { it.quantity }
    val totalAccessoryStockValue = accessories.sumOf { it.purchasePrice * it.quantity }

    val pendingRepairs = repairJobs.filter { it.status == "Pending" || it.status == "Repairing" }

    val todayInvoices = invoices.filter { it.date == todayDate }
    val todaySalesAmount = todayInvoices.sumOf { it.finalTotal }

    val todayRepairs = repairJobs.filter { it.date == todayDate }
    val todayRepairIncome = todayRepairs.sumOf { it.advancePayment } + repairJobs.filter { it.status == "Delivered" && it.date == todayDate }.sumOf { it.cost - it.advancePayment }

    val totalCustomerRemainingBalance = invoices.sumOf { it.remainingBalance } + repairJobs.sumOf { it.remainingPayment }

    val todayExpensesList = expenses.filter { it.date == todayDate }
    val todayExpensesAmount = todayExpensesList.sumOf { it.amount }

    val todayTotalRevenue = todaySalesAmount + todayRepairIncome
    val todayProfit = (todaySalesAmount * 0.25) + todayRepairIncome - todayExpensesAmount // Estimated 25% profit margin on sales + full repair labor income minus expenses

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Shop Header Card
        item {
            OutlinedCard(
                colors = CardDefaults.outlinedCardColors(containerColor = DarkSurface),
                border = BorderStroke(1.dp, DarkBorder),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(GoldPrimary, CircleShape)
                            )
                            Text(
                                text = "KASHIF MOBILE & REPAIR",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldPrimary,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Offline Management • Date: $todayDate",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    Surface(
                        color = GoldPrimary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "ACTIVE SHOP",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Daily Financial Summary Card
        item {
            OutlinedCard(
                colors = CardDefaults.outlinedCardColors(containerColor = DarkSurface),
                border = BorderStroke(1.dp, DarkBorder),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(
                                imageVector = Icons.Default.AccountBalance,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Daily Financial Summary",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Surface(
                            color = if (todayProfit >= 0) GoldPrimary.copy(alpha = 0.2f) else Color(0xFFFF8A80).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Est. Net: Rs. ${todayProfit.toInt()}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (todayProfit >= 0) GoldPrimary else Color(0xFFFF8A80),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Revenue", fontSize = 11.sp, color = TextMuted)
                            Text(
                                "Rs. ${todayTotalRevenue.toInt()}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldPrimary
                            )
                        }
                        Column {
                            Text("Sales Amount", fontSize = 11.sp, color = TextMuted)
                            Text(
                                "Rs. ${todaySalesAmount.toInt()}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF81C784)
                            )
                        }
                        Column {
                            Text("Repair Income", fontSize = 11.sp, color = TextMuted)
                            Text(
                                "Rs. ${todayRepairIncome.toInt()}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF64B5F6)
                            )
                        }
                        Column {
                            Text("Expenses", fontSize = 11.sp, color = TextMuted)
                            Text(
                                "Rs. ${todayExpensesAmount.toInt()}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFE57373)
                            )
                        }
                    }
                }
            }
        }

        // Stat Grid (2 x 4)
        item {
            Text(
                text = "Key Business Metrics",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StatCard(
                        title = "📱 Mobile Stock",
                        value = "$totalMobilesQty Items",
                        subtitle = "Val: Rs. ${totalMobileStockValue.toInt()}",
                        icon = Icons.Default.PhoneAndroid,
                        onClick = { onNavigate(ShopScreen.MOBILES) },
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "🎧 Accessories",
                        value = "$totalAccessoriesQty Items",
                        subtitle = "Val: Rs. ${totalAccessoryStockValue.toInt()}",
                        icon = Icons.Default.Headset,
                        onClick = { onNavigate(ShopScreen.ACCESSORIES) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StatCard(
                        title = "🔧 Pending Repairs",
                        value = "${pendingRepairs.size} Jobs",
                        subtitle = "Click to view jobs",
                        icon = Icons.Default.Build,
                        accentColor = Color(0xFFFFB74D),
                        onClick = { onNavigate(ShopScreen.REPAIRS) },
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "💰 Today's Sales",
                        value = "Rs. ${todaySalesAmount.toInt()}",
                        subtitle = "${todayInvoices.size} Invoices today",
                        icon = Icons.Default.ShoppingCart,
                        accentColor = Color(0xFF81C784),
                        onClick = { onNavigate(ShopScreen.SALES) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StatCard(
                        title = "💵 Repair Income",
                        value = "Rs. ${todayRepairIncome.toInt()}",
                        subtitle = "Today's repair revenue",
                        icon = Icons.Default.MonetizationOn,
                        accentColor = Color(0xFF64B5F6),
                        onClick = { onNavigate(ShopScreen.REPAIRS) },
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "💳 Cust. Remaining",
                        value = "Rs. ${totalCustomerRemainingBalance.toInt()}",
                        subtitle = "Total pending dues",
                        icon = Icons.Default.People,
                        accentColor = Color(0xFFFF8A80),
                        onClick = { onNavigate(ShopScreen.CUSTOMERS) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StatCard(
                        title = "📉 Today's Expenses",
                        value = "Rs. ${todayExpensesAmount.toInt()}",
                        subtitle = "${todayExpensesList.size} Expenses today",
                        icon = Icons.Default.AccountBalanceWallet,
                        accentColor = Color(0xFFE57373),
                        onClick = { onNavigate(ShopScreen.EXPENSES) },
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "📊 Today's Profit",
                        value = "Rs. ${todayProfit.toInt()}",
                        subtitle = "Est. Net Earnings",
                        icon = Icons.Default.TrendingUp,
                        accentColor = GoldPrimary,
                        onClick = { onNavigate(ShopScreen.REPORTS) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Quick Shortcut Action Buttons
        item {
            Text(
                text = "Quick Actions",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    Button(
                        onClick = { onNavigate(ShopScreen.SALES) },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PointOfSale, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("New Sale", fontWeight = FontWeight.Bold)
                    }
                }
                item {
                    OutlinedButton(
                        onClick = { onNavigate(ShopScreen.MOBILES) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                        border = BorderStroke(1.dp, DarkBorder),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Mobile")
                    }
                }
                item {
                    OutlinedButton(
                        onClick = { onNavigate(ShopScreen.ACCESSORIES) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                        border = BorderStroke(1.dp, DarkBorder),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Accessory")
                    }
                }
                item {
                    OutlinedButton(
                        onClick = { onNavigate(ShopScreen.REPAIRS) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                        border = BorderStroke(1.dp, DarkBorder),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Build, contentDescription = null, tint = Color(0xFFFFB74D), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("New Repair Job")
                    }
                }
                item {
                    OutlinedButton(
                        onClick = { onNavigate(ShopScreen.EXPENSES) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                        border = BorderStroke(1.dp, DarkBorder),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Color(0xFFE57373), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Expense")
                    }
                }
            }
        }

        // Active Repair Jobs Preview
        item {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Active Repair Jobs",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                TextButton(onClick = { onNavigate(ShopScreen.REPAIRS) }) {
                    Text("View All", color = GoldPrimary, fontWeight = FontWeight.SemiBold)
                }
            }

            if (pendingRepairs.isEmpty()) {
                OutlinedCard(
                    colors = CardDefaults.outlinedCardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.dp, DarkBorder),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "No pending repair jobs at the moment.",
                        fontSize = 13.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    pendingRepairs.take(3).forEach { job ->
                        OutlinedCard(
                            colors = CardDefaults.outlinedCardColors(containerColor = DarkSurface),
                            border = BorderStroke(1.dp, DarkBorder),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${job.brand} ${job.model}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldPrimary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Cust: ${job.customerName} • ${job.customerPhone}",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                    Text(
                                        text = "Problem: ${job.problem}",
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Surface(
                                        color = when (job.status) {
                                            "Pending" -> Color(0xFFFFB74D)
                                            "Repairing" -> Color(0xFF64B5F6)
                                            else -> GoldPrimary
                                        },
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = job.status,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Rs. ${job.cost.toInt()}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

