package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GoldPrimary
import com.example.ui.viewmodel.ShopViewModel

val RepairStatuses = listOf("All", "Pending", "Repairing", "Completed", "Delivered", "Cancelled")

@Composable
fun RepairsScreen(
    viewModel: ShopViewModel,
    onOpenDailyRepairs: () -> Unit = {}
) {
    val repairJobs by viewModel.repairJobs.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("All") }

    var showAddEditDialog by remember { mutableStateOf(false) }
    var selectedJobForEdit by remember { mutableStateOf<RepairJob?>(null) }
    var jobToDelete by remember { mutableStateOf<RepairJob?>(null) }
    var jobForPayment by remember { mutableStateOf<RepairJob?>(null) }

    val filteredJobs = remember(repairJobs, searchQuery, selectedStatus) {
        repairJobs.filter { job ->
            val matchesStatus = (selectedStatus == "All" || job.status.equals(selectedStatus, ignoreCase = true))
            val matchesQuery = searchQuery.isBlank() ||
                    job.customerName.contains(searchQuery, ignoreCase = true) ||
                    job.customerPhone.contains(searchQuery, ignoreCase = true) ||
                    job.brand.contains(searchQuery, ignoreCase = true) ||
                    job.model.contains(searchQuery, ignoreCase = true) ||
                    job.imei.contains(searchQuery, ignoreCase = true) ||
                    job.problem.contains(searchQuery, ignoreCase = true)
            matchesStatus && matchesQuery
        }
    }

    val pendingCount = repairJobs.count { it.status == "Pending" }
    val repairingCount = repairJobs.count { it.status == "Repairing" }
    val completedCount = repairJobs.count { it.status == "Completed" }
    val deliveredCount = repairJobs.count { it.status == "Delivered" }

    val totalOutstandingDues = repairJobs.filter { it.status != "Delivered" && it.status != "Cancelled" }.sumOf { it.remainingPayment }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedJobForEdit = null
                    showAddEditDialog = true
                },
                containerColor = GoldPrimary,
                contentColor = Color.Black
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "New Repair Job")
            }
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Header Card with button to Daily Repair Record
            OutlinedCard(
                colors = CardDefaults.outlinedCardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("🔧 Mobile Repairing Hub", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)

                        Button(
                            onClick = onOpenDailyRepairs,
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant, contentColor = GoldPrimary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Daily Record", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text("Pending", fontSize = 11.sp, color = Color.Gray)
                            Text("$pendingCount", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFB74D))
                        }
                        Column {
                            Text("Repairing", fontSize = 11.sp, color = Color.Gray)
                            Text("$repairingCount", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64B5F6))
                        }
                        Column {
                            Text("Completed", fontSize = 11.sp, color = Color.Gray)
                            Text("$completedCount", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF81C784))
                        }
                        Column {
                            Text("Delivered", fontSize = 11.sp, color = Color.Gray)
                            Text("$deliveredCount", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.LightGray)
                        }
                        Column {
                            Text("Outstanding Dues", fontSize = 11.sp, color = Color.Gray)
                            Text("Rs. ${totalOutstandingDues.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF8A80))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search customer, phone, device, problem...") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = GoldPrimary) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = DarkBorder,
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Status Filter Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                RepairStatuses.forEach { status ->
                    val isSelected = status == selectedStatus
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedStatus = status },
                        label = { Text(status) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldPrimary,
                            selectedLabelColor = Color.Black,
                            containerColor = DarkSurface,
                            labelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Repair Jobs List
            if (filteredJobs.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Text(
                        text = "No repair jobs found.",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredJobs, key = { it.id }) { job ->
                        OutlinedCard(
                            colors = CardDefaults.outlinedCardColors(containerColor = DarkSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${job.brand} ${job.model}",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldPrimary
                                        )
                                        Text(
                                            text = "Cust: ${job.customerName} (${job.customerPhone})",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White
                                        )
                                        if (job.imei.isNotBlank()) {
                                            Text("IMEI: ${job.imei}", fontSize = 11.sp, color = Color.Gray)
                                        }
                                    }

                                    // Status Badge & Dropdown Selector
                                    Surface(
                                        color = when (job.status) {
                                            "Pending" -> Color(0xFFFFB74D)
                                            "Repairing" -> Color(0xFF64B5F6)
                                            "Completed" -> Color(0xFF81C784)
                                            "Delivered" -> Color(0xFFB0BEC5)
                                            else -> Color(0xFFE57373)
                                        },
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = job.status,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Problem: ${job.problem}", fontSize = 12.sp, color = Color.LightGray)
                                if (job.repairDetails.isNotBlank()) {
                                    Text("Work: ${job.repairDetails}", fontSize = 11.sp, color = Color.Gray)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Quick Status Action Step Bar
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    when (job.status) {
                                        "Pending" -> {
                                            Button(
                                                onClick = {
                                                    viewModel.updateRepairJob(job.copy(status = "Repairing"))
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64B5F6), contentColor = Color.Black),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(imageVector = Icons.Default.Build, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Start Repair", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        "Repairing" -> {
                                            Button(
                                                onClick = {
                                                    viewModel.updateRepairJob(job.copy(status = "Completed"))
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF81C784), contentColor = Color.Black),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Mark Completed", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        "Completed" -> {
                                            Button(
                                                onClick = {
                                                    if (job.remainingPayment > 0) {
                                                        jobForPayment = job
                                                    } else {
                                                        viewModel.updateRepairJob(job.copy(status = "Delivered"))
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(imageVector = Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Deliver Device", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }

                                    // Collect Payment Button
                                    OutlinedButton(
                                        onClick = { jobForPayment = job },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldPrimary),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Payment", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 8.dp))

                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column {
                                        Text("Cost: Rs. ${job.cost.toInt()} | Paid: Rs. ${job.advancePayment.toInt()}", fontSize = 12.sp, color = Color.LightGray)
                                        Text(
                                            "Remaining Due: Rs. ${job.remainingPayment.toInt()}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (job.remainingPayment > 0) Color(0xFFFF8A80) else Color(0xFF81C784)
                                        )
                                    }

                                    Row {
                                        IconButton(
                                            onClick = {
                                                selectedJobForEdit = job
                                                showAddEditDialog = true
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = GoldPrimary, modifier = Modifier.size(18.dp))
                                        }
                                        IconButton(
                                            onClick = { jobToDelete = job },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFE57373), modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add / Edit Job Dialog
    if (showAddEditDialog) {
        AddEditRepairJobDialog(
            job = selectedJobForEdit,
            onDismiss = { showAddEditDialog = false },
            onSave = { job ->
                if (selectedJobForEdit == null) {
                    viewModel.addRepairJob(job)
                } else {
                    viewModel.updateRepairJob(job)
                }
                showAddEditDialog = false
            }
        )
    }

    // Collect / Update Payment Dialog
    if (jobForPayment != null) {
        CollectPaymentDialog(
            job = jobForPayment!!,
            onDismiss = { jobForPayment = null },
            onPaymentSaved = { updatedJob ->
                viewModel.updateRepairJob(updatedJob)
                jobForPayment = null
            }
        )
    }

    // Delete Job
    if (jobToDelete != null) {
        AlertDialog(
            onDismissRequest = { jobToDelete = null },
            title = { Text("Delete Repair Job", color = GoldPrimary) },
            text = { Text("Are you sure you want to delete repair job for ${jobToDelete?.customerName} (${jobToDelete?.brand} ${jobToDelete?.model})?") },
            confirmButton = {
                Button(
                    onClick = {
                        jobToDelete?.let { viewModel.deleteRepairJob(it.id) }
                        jobToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE57373))
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { jobToDelete = null }) {
                    Text("Cancel", color = Color.White)
                }
            },
            containerColor = DarkSurface
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditRepairJobDialog(
    job: RepairJob?,
    onDismiss: () -> Unit,
    onSave: (RepairJob) -> Unit
) {
    var customerName by remember { mutableStateOf(job?.customerName ?: "") }
    var customerPhone by remember { mutableStateOf(job?.customerPhone ?: "") }
    var brand by remember { mutableStateOf(job?.brand ?: "") }
    var model by remember { mutableStateOf(job?.model ?: "") }
    var imei by remember { mutableStateOf(job?.imei ?: "") }
    var problem by remember { mutableStateOf(job?.problem ?: "") }
    var repairDetails by remember { mutableStateOf(job?.repairDetails ?: "") }
    var costStr by remember { mutableStateOf(job?.cost?.let { if (it == 0.0) "" else it.toString() } ?: "") }
    var advanceStr by remember { mutableStateOf(job?.advancePayment?.let { if (it == 0.0) "" else it.toString() } ?: "") }
    var expectedDate by remember { mutableStateOf(job?.expectedDeliveryDate ?: "") }
    var status by remember { mutableStateOf(job?.status ?: "Pending") }
    var notes by remember { mutableStateOf(job?.notes ?: "") }

    val costVal = costStr.toDoubleOrNull() ?: 0.0
    val advanceVal = advanceStr.toDoubleOrNull() ?: 0.0
    val remainingVal = (costVal - advanceVal).coerceAtLeast(0.0)

    var statusDropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (job == null) "New Repair Job" else "Edit Repair Job", color = GoldPrimary) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = customerName,
                            onValueChange = { customerName = it },
                            label = { Text("Customer Name") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = customerPhone,
                            onValueChange = { customerPhone = it },
                            label = { Text("Phone Number") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = brand,
                            onValueChange = { brand = it },
                            label = { Text("Mobile Brand") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = model,
                            onValueChange = { model = it },
                            label = { Text("Mobile Model") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = imei,
                        onValueChange = { imei = it },
                        label = { Text("IMEI (Optional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = problem,
                        onValueChange = { problem = it },
                        label = { Text("Customer Problem (e.g. Display broken)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = repairDetails,
                        onValueChange = { repairDetails = it },
                        label = { Text("Repair Details / Parts replaced") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = costStr,
                            onValueChange = { costStr = it },
                            label = { Text("Repair Cost") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = advanceStr,
                            onValueChange = { advanceStr = it },
                            label = { Text("Advance Paid") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.padding(10.dp).fillMaxWidth()
                        ) {
                            Text("Calculated Remaining:", fontSize = 12.sp, color = Color.LightGray)
                            Text("Rs. ${remainingVal.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                        }
                    }
                }
                item {
                    ExposedDropdownMenuBox(
                        expanded = statusDropdownExpanded,
                        onExpandedChange = { statusDropdownExpanded = !statusDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = status,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Repair Status") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusDropdownExpanded) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = statusDropdownExpanded,
                            onDismissRequest = { statusDropdownExpanded = false }
                        ) {
                            RepairStatuses.filter { it != "All" }.forEach { st ->
                                DropdownMenuItem(
                                    text = { Text(st) },
                                    onClick = {
                                        status = st
                                        statusDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                item {
                    OutlinedTextField(
                        value = expectedDate,
                        onValueChange = { expectedDate = it },
                        label = { Text("Expected Delivery Date") },
                        placeholder = { Text("YYYY-MM-DD") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (customerName.isNotBlank() && brand.isNotBlank()) {
                        val jobObj = RepairJob(
                            id = job?.id ?: 0,
                            customerName = customerName.trim(),
                            customerPhone = customerPhone.trim(),
                            brand = brand.trim(),
                            model = model.trim(),
                            imei = imei.trim(),
                            problem = problem.trim(),
                            repairDetails = repairDetails.trim(),
                            cost = costVal,
                            advancePayment = advanceVal,
                            date = job?.date ?: ShopViewModel.getTodayDateString(),
                            expectedDeliveryDate = expectedDate.trim(),
                            status = status,
                            notes = notes.trim()
                        )
                        onSave(jobObj)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
            ) {
                Text("Save Job")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.White)
            }
        },
        containerColor = DarkSurface
    )
}

@Composable
fun CollectPaymentDialog(
    job: RepairJob,
    onDismiss: () -> Unit,
    onPaymentSaved: (RepairJob) -> Unit
) {
    var newAmountStr by remember { mutableStateOf("") }
    var markDelivered by remember { mutableStateOf(job.status == "Completed") }

    val additionalVal = newAmountStr.toDoubleOrNull() ?: 0.0
    val totalPaidNew = job.advancePayment + additionalVal
    val remainingNew = (job.cost - totalPaidNew).coerceAtLeast(0.0)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Collect Payment", color = GoldPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "${job.brand} ${job.model} • Cust: ${job.customerName}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Total Repair Cost:", fontSize = 12.sp, color = Color.Gray)
                            Text("Rs. ${job.cost.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Already Paid:", fontSize = 12.sp, color = Color.Gray)
                            Text("Rs. ${job.advancePayment.toInt()}", fontSize = 12.sp, color = Color(0xFF81C784))
                        }
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Current Due Balance:", fontSize = 12.sp, color = Color.Gray)
                            Text("Rs. ${job.remainingPayment.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF8A80))
                        }
                    }
                }

                OutlinedTextField(
                    value = newAmountStr,
                    onValueChange = { newAmountStr = it },
                    label = { Text("Additional Payment Amount Collected") },
                    placeholder = { Text("e.g. ${job.remainingPayment.toInt()}") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = DarkBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Quick Pay Full Remaining Button
                if (job.remainingPayment > 0) {
                    OutlinedButton(
                        onClick = { newAmountStr = job.remainingPayment.toInt().toString() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldPrimary),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Settle Full Remaining Balance (Rs. ${job.remainingPayment.toInt()})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("New Total Paid:", fontSize = 12.sp, color = Color.Gray)
                            Text("Rs. ${totalPaidNew.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                        }
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("New Remaining Due:", fontSize = 12.sp, color = Color.Gray)
                            Text(
                                "Rs. ${remainingNew.toInt()}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (remainingNew > 0) Color(0xFFFF8A80) else Color(0xFF81C784)
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Checkbox(
                        checked = markDelivered,
                        onCheckedChange = { markDelivered = it },
                        colors = CheckboxDefaults.colors(checkedColor = GoldPrimary)
                    )
                    Text("Mark repair status as Delivered", fontSize = 12.sp, color = Color.White)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalStatus = if (markDelivered) "Delivered" else job.status
                    val updated = job.copy(
                        advancePayment = totalPaidNew,
                        status = finalStatus
                    )
                    onPaymentSaved(updated)
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
            ) {
                Text("Confirm Payment")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.White)
            }
        },
        containerColor = DarkSurface
    )
}

