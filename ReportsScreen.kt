package com.example.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GoldPrimary
import com.example.ui.viewmodel.ShopViewModel

@Composable
fun ReportsScreen(viewModel: ShopViewModel) {
    val mobiles by viewModel.mobiles.collectAsState()
    val accessories by viewModel.accessories.collectAsState()
    val repairJobs by viewModel.repairJobs.collectAsState()
    val invoices by viewModel.invoices.collectAsState()
    val expenses by viewModel.expenses.collectAsState()

    var filterDate by remember { mutableStateOf(ShopViewModel.getTodayDateString()) }
    val currentMonth = remember(filterDate) { filterDate.take(7) } // e.g., 2026-08

    // Calculations
    // 1. Daily Sales
    val dailySalesInvoices = invoices.filter { it.date == filterDate }
    val dailySalesTotal = dailySalesInvoices.sumOf { it.finalTotal }

    // 2. Monthly Sales
    val monthlySalesInvoices = invoices.filter { it.date.startsWith(currentMonth) }
    val monthlySalesTotal = monthlySalesInvoices.sumOf { it.finalTotal }

    // 3. Mobile Stock
    val totalMobileQty = mobiles.sumOf { it.quantity }
    val mobileStockBuyVal = mobiles.sumOf { it.purchasePrice * it.quantity }
    val mobileStockSellVal = mobiles.sumOf { it.salePrice * it.quantity }

    // 4. Accessories Stock
    val totalAccessoryQty = accessories.sumOf { it.quantity }
    val accessoryStockBuyVal = accessories.sumOf { it.purchasePrice * it.quantity }
    val accessoryStockSellVal = accessories.sumOf { it.salePrice * it.quantity }

    // 5. Daily Repairs
    val dailyRepairsList = repairJobs.filter { it.date == filterDate }
    val dailyRepairsIncome = dailyRepairsList.sumOf { it.advancePayment } + dailyRepairsList.filter { it.status == "Delivered" || it.status == "Completed" }.sumOf { it.cost - it.advancePayment }

    // 6. Monthly Repairs
    val monthlyRepairsList = repairJobs.filter { it.date.startsWith(currentMonth) }
    val monthlyRepairsIncome = monthlyRepairsList.sumOf { it.advancePayment } + monthlyRepairsList.filter { it.status == "Delivered" || it.status == "Completed" }.sumOf { it.cost - it.advancePayment }

    // 7. Expenses
    val dailyExpenses = expenses.filter { it.date == filterDate }.sumOf { it.amount }
    val monthlyExpenses = expenses.filter { it.date.startsWith(currentMonth) }.sumOf { it.amount }

    // 8. Customer Balances
    val totalCustomerBalance = invoices.sumOf { it.remainingBalance } + repairJobs.sumOf { it.remainingPayment }

    // 9. Profit (Monthly)
    val estimatedGrossMargin = monthlySalesTotal * 0.25
    val netMonthlyProfit = estimatedGrossMargin + monthlyRepairsIncome - monthlyExpenses

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Header
        item {
            Text("📊 Business Reports & Analytics", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
            Text("Filter analytics by custom date", fontSize = 12.sp, color = Color.Gray)
        }

        // Date Filter Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(12.dp).fillMaxWidth()
                ) {
                    Text("Filter Date:", fontSize = 13.sp, color = Color.LightGray)
                    OutlinedTextField(
                        value = filterDate,
                        onValueChange = { filterDate = it },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = DarkBorder
                        ),
                        modifier = Modifier.width(160.dp)
                    )
                }
            }
        }

        // Monthly Net Profit Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GoldPrimary, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("💰 Net Profit Report ($currentMonth)", fontSize = 14.sp, color = Color.LightGray)
                    Text(
                        text = "Rs. ${netMonthlyProfit.toInt()}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                    Text("Calculation: (Sales Margin + Repair Income) - Expenses", fontSize = 10.sp, color = Color.Gray, modifier = Modifier.padding(top = 2.dp))
                }
            }
        }

        // Report Sections
        item {
            ReportCardItem(
                title = "Sales Report",
                icon = Icons.Default.ShoppingCart,
                rows = listOf(
                    "Daily Sales ($filterDate)" to "Rs. ${dailySalesTotal.toInt()} (${dailySalesInvoices.size} inv)",
                    "Monthly Sales ($currentMonth)" to "Rs. ${monthlySalesTotal.toInt()} (${monthlySalesInvoices.size} inv)"
                )
            )
        }

        item {
            ReportCardItem(
                title = "Repairing Report",
                icon = Icons.Default.Build,
                rows = listOf(
                    "Daily Repairs ($filterDate)" to "Rs. ${dailyRepairsIncome.toInt()} (${dailyRepairsList.size} jobs)",
                    "Monthly Repairs ($currentMonth)" to "Rs. ${monthlyRepairsIncome.toInt()} (${monthlyRepairsList.size} jobs)"
                )
            )
        }

        item {
            ReportCardItem(
                title = "Mobile Stock Valuation",
                icon = Icons.Default.PhoneAndroid,
                rows = listOf(
                    "Total Units in Stock" to "$totalMobileQty Units",
                    "Stock Purchase Cost" to "Rs. ${mobileStockBuyVal.toInt()}",
                    "Expected Sales Revenue" to "Rs. ${mobileStockSellVal.toInt()}"
                )
            )
        }

        item {
            ReportCardItem(
                title = "Accessories Stock Valuation",
                icon = Icons.Default.Headset,
                rows = listOf(
                    "Total Accessories Stock" to "$totalAccessoryQty Items",
                    "Stock Purchase Cost" to "Rs. ${accessoryStockBuyVal.toInt()}",
                    "Expected Sales Revenue" to "Rs. ${accessoryStockSellVal.toInt()}"
                )
            )
        }

        item {
            ReportCardItem(
                title = "Expenses & Receivables",
                icon = Icons.Default.AccountBalanceWallet,
                rows = listOf(
                    "Daily Expenses ($filterDate)" to "Rs. ${dailyExpenses.toInt()}",
                    "Monthly Expenses ($currentMonth)" to "Rs. ${monthlyExpenses.toInt()}",
                    "Total Outstanding Customer Dues" to "Rs. ${totalCustomerBalance.toInt()}"
                )
            )
        }
    }
}

@Composable
fun ReportCardItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    rows: List<Pair<String, String>>
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = GoldPrimary, modifier = Modifier.size(20.dp))
                Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
            }

            Divider(color = DarkBorder, modifier = Modifier.padding(vertical = 8.dp))

            rows.forEach { (label, value) ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
                ) {
                    Text(label, fontSize = 12.sp, color = Color.LightGray)
                    Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
