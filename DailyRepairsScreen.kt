package com.example.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.entity.RepairJob
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GoldPrimary
import com.example.ui.viewmodel.ShopViewModel

@Composable
fun DailyRepairsScreen(
    viewModel: ShopViewModel,
    onBack: () -> Unit = {}
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val repairJobs by viewModel.repairJobs.collectAsState()

    var editingJob by remember { mutableStateOf<RepairJob?>(null) }

    val dailyJobs = remember(repairJobs, selectedDate) {
        repairJobs.filter { it.date == selectedDate }
    }

    val totalJobs = dailyJobs.size
    val completedCount = dailyJobs.count { it.status == "Completed" || it.status == "Delivered" }
    val pendingCount = dailyJobs.count { it.status == "Pending" || it.status == "Repairing" }

    val totalAdvance = dailyJobs.sumOf { it.advancePayment }
    val totalRemaining = dailyJobs.sumOf { it.remainingPayment }
    val totalRepairIncome = dailyJobs.sumOf { it.advancePayment } + dailyJobs.filter { it.status == "Delivered" || it.status == "Completed" }.sumOf { it.cost - it.advancePayment }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Back Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = GoldPrimary)
            }
            Text(
                text = "Daily Repair Record",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = GoldPrimary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Date Selector Bar
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.DateRange, contentDescription = null, tint = GoldPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Selected Date:", fontSize = 13.sp, color = Color.Gray)
                }

                OutlinedTextField(
                    value = selectedDate,
                    onValueChange = { viewModel.setSelectedDate(it) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = DarkBorder,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.width(160.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Daily Summary Statistics Card
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("📊 Summary for $selectedDate", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text("Total Jobs", fontSize = 11.sp, color = Color.Gray)
                        Text("$totalJobs", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Column {
                        Text("Completed", fontSize = 11.sp, color = Color.Gray)
                        Text("$completedCount", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF81C784))
                    }
                    Column {
                        Text("Pending", fontSize = 11.sp, color = Color.Gray)
                        Text("$pendingCount", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFB74D))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = DarkBorder)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text("Total Income", fontSize = 11.sp, color = Color.Gray)
                        Text("Rs. ${totalRepairIncome.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                    }
                    Column {
                        Text("Advance Recv.", fontSize = 11.sp, color = Color.Gray)
                        Text("Rs. ${totalAdvance.toInt()}", fontSize = 13.sp, color = Color.LightGray)
                    }
                    Column {
                        Text("Remaining", fontSize = 11.sp, color = Color.Gray)
                        Text("Rs. ${totalRemaining.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF8A80))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Daily Repair Jobs List
        if (dailyJobs.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Text("No repair jobs recorded on $selectedDate.", color = Color.Gray, fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(dailyJobs, key = { it.id }) { job ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("${job.brand} ${job.model}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                                    Text("Customer: ${job.customerName} (${job.customerPhone})", fontSize = 12.sp, color = Color.White)
                                    Text("Problem: ${job.problem}", fontSize = 11.sp, color = Color.Gray)
                                }

                                Surface(
                                    color = when (job.status) {
                                        "Pending" -> Color(0xFFFFB74D)
                                        "Repairing" -> Color(0xFF64B5F6)
                                        "Completed" -> Color(0xFF81C784)
                                        else -> Color(0xFFB0BEC5)
                                    },
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = job.status,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Divider(color = DarkBorder, modifier = Modifier.padding(vertical = 8.dp))

                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Cost: Rs. ${job.cost.toInt()} | Adv: Rs. ${job.advancePayment.toInt()} | Rem: Rs. ${job.remainingPayment.toInt()}",
                                    fontSize = 11.sp,
                                    color = Color.LightGray
                                )

                                IconButton(
                                    onClick = { editingJob = job },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Record", tint = GoldPrimary, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (editingJob != null) {
        AddEditRepairJobDialog(
            job = editingJob,
            onDismiss = { editingJob = null },
            onSave = { updated ->
                viewModel.updateRepairJob(updated)
                editingJob = null
            }
        )
    }
}
