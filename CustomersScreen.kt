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
import com.example.data.entity.Customer
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GoldPrimary
import com.example.ui.viewmodel.ShopViewModel

@Composable
fun CustomersScreen(viewModel: ShopViewModel) {
    val customers by viewModel.customers.collectAsState()
    val invoices by viewModel.invoices.collectAsState()
    val repairJobs by viewModel.repairJobs.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showAddEditDialog by remember { mutableStateOf(false) }
    var selectedCustomerForEdit by remember { mutableStateOf<Customer?>(null) }
    var customerToDelete by remember { mutableStateOf<Customer?>(null) }
    var viewingCustomerHistory by remember { mutableStateOf<Customer?>(null) }

    val filteredCustomers = remember(customers, searchQuery) {
        if (searchQuery.isBlank()) customers
        else customers.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.phone.contains(searchQuery, ignoreCase = true) ||
                    it.address.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedCustomerForEdit = null
                    showAddEditDialog = true
                },
                containerColor = GoldPrimary,
                contentColor = Color.Black
            ) {
                Icon(imageVector = Icons.Default.PersonAdd, contentDescription = "Add Customer")
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
            // Header
            Text("👥 Customer Directory", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
            Text("${customers.size} Registered Customers", fontSize = 12.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search customer name, phone, address...") },
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

            Spacer(modifier = Modifier.height(12.dp))

            // Customer List
            if (filteredCustomers.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth().weight(1f)
                ) {
                    Text("No customers found.", color = Color.Gray, fontSize = 14.sp)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredCustomers, key = { it.id }) { customer ->
                        val customerInvoices = invoices.filter {
                            it.customerName.equals(customer.name, ignoreCase = true) ||
                                    (it.customerPhone.isNotBlank() && it.customerPhone == customer.phone)
                        }
                        val customerRepairs = repairJobs.filter {
                            it.customerName.equals(customer.name, ignoreCase = true) ||
                                    (it.customerPhone.isNotBlank() && it.customerPhone == customer.phone)
                        }

                        val totalPurchases = customerInvoices.sumOf { it.finalTotal }
                        val totalRepairCharges = customerRepairs.sumOf { it.cost }
                        val totalPaid = customerInvoices.sumOf { it.paidAmount } + customerRepairs.sumOf { it.advancePayment } + customerRepairs.filter { it.status == "Delivered" }.sumOf { it.cost - it.advancePayment }
                        val remainingBalance = (totalPurchases + totalRepairCharges) - totalPaid

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
                                        Text(customer.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                                        Text("Phone: ${customer.phone}", fontSize = 13.sp, color = Color.White)
                                        if (customer.address.isNotBlank()) {
                                            Text("Address: ${customer.address}", fontSize = 11.sp, color = Color.Gray)
                                        }
                                    }

                                    Button(
                                        onClick = { viewingCustomerHistory = customer },
                                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant, contentColor = GoldPrimary),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.History, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("History", fontSize = 11.sp)
                                    }
                                }

                                Divider(color = DarkBorder, modifier = Modifier.padding(vertical = 8.dp))

                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column {
                                        Text("Purchases: Rs. ${totalPurchases.toInt()}", fontSize = 11.sp, color = Color.LightGray)
                                        Text("Repairs: Rs. ${totalRepairCharges.toInt()}", fontSize = 11.sp, color = Color.LightGray)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Paid: Rs. ${totalPaid.toInt()}", fontSize = 11.sp, color = Color(0xFF81C784))
                                        Text("Balance Due: Rs. ${remainingBalance.coerceAtLeast(0.0).toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (remainingBalance > 0) Color(0xFFFFB74D) else Color.Gray)
                                    }
                                }

                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                                ) {
                                    IconButton(
                                        onClick = {
                                            selectedCustomerForEdit = customer
                                            showAddEditDialog = true
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = GoldPrimary, modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(
                                        onClick = { customerToDelete = customer },
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

    // Add / Edit Dialog
    if (showAddEditDialog) {
        AddEditCustomerDialog(
            customer = selectedCustomerForEdit,
            onDismiss = { showAddEditDialog = false },
            onSave = { cust ->
                if (selectedCustomerForEdit == null) {
                    viewModel.addCustomer(cust)
                } else {
                    viewModel.updateCustomer(cust)
                }
                showAddEditDialog = false
            }
        )
    }

    // Customer History Modal
    if (viewingCustomerHistory != null) {
        val cust = viewingCustomerHistory!!
        val customerInvoices = invoices.filter {
            it.customerName.equals(cust.name, ignoreCase = true) ||
                    (it.customerPhone.isNotBlank() && it.customerPhone == cust.phone)
        }
        val customerRepairs = repairJobs.filter {
            it.customerName.equals(cust.name, ignoreCase = true) ||
                    (it.customerPhone.isNotBlank() && it.customerPhone == cust.phone)
        }

        AlertDialog(
            onDismissRequest = { viewingCustomerHistory = null },
            title = { Text("Customer History: ${cust.name}", color = GoldPrimary) },
            text = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(340.dp)
                ) {
                    item {
                        Text("Sales History (${customerInvoices.size})", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    if (customerInvoices.isEmpty()) {
                        item { Text("No purchases recorded.", fontSize = 12.sp, color = Color.Gray) }
                    } else {
                        items(customerInvoices) { inv ->
                            Card(colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("Invoice #${inv.invoiceNumber} • ${inv.date}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                                    Text("Total: Rs. ${inv.finalTotal.toInt()} | Paid: Rs. ${inv.paidAmount.toInt()}", fontSize = 11.sp, color = Color.LightGray)
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Repair History (${customerRepairs.size})", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    if (customerRepairs.isEmpty()) {
                        item { Text("No repairs recorded.", fontSize = 12.sp, color = Color.Gray) }
                    } else {
                        items(customerRepairs) { rep ->
                            Card(colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("${rep.brand} ${rep.model} • Status: ${rep.status}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                                    Text("Problem: ${rep.problem}", fontSize = 11.sp, color = Color.White)
                                    Text("Cost: Rs. ${rep.cost.toInt()} | Rem: Rs. ${rep.remainingPayment.toInt()}", fontSize = 11.sp, color = Color.LightGray)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { viewingCustomerHistory = null }) {
                    Text("Close", color = Color.White)
                }
            },
            containerColor = DarkSurface
        )
    }

    // Delete confirmation
    if (customerToDelete != null) {
        AlertDialog(
            onDismissRequest = { customerToDelete = null },
            title = { Text("Delete Customer", color = GoldPrimary) },
            text = { Text("Are you sure you want to delete ${customerToDelete?.name}?") },
            confirmButton = {
                Button(
                    onClick = {
                        customerToDelete?.let { viewModel.deleteCustomer(it.id) }
                        customerToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE57373))
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { customerToDelete = null }) {
                    Text("Cancel", color = Color.White)
                }
            },
            containerColor = DarkSurface
        )
    }
}

@Composable
fun AddEditCustomerDialog(
    customer: Customer?,
    onDismiss: () -> Unit,
    onSave: (Customer) -> Unit
) {
    var name by remember { mutableStateOf(customer?.name ?: "") }
    var phone by remember { mutableStateOf(customer?.phone ?: "") }
    var address by remember { mutableStateOf(customer?.address ?: "") }
    var notes by remember { mutableStateOf(customer?.notes ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (customer == null) "Add Customer" else "Edit Customer", color = GoldPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Customer Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val custObj = Customer(
                            id = customer?.id ?: 0,
                            name = name.trim(),
                            phone = phone.trim(),
                            address = address.trim(),
                            notes = notes.trim()
                        )
                        onSave(custObj)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
            ) {
                Text("Save")
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
