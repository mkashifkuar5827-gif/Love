package com.example.ui.screens

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
import com.example.data.entity.Expense
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GoldPrimary
import com.example.ui.viewmodel.ShopViewModel

val ExpenseCategories = listOf("All", "Shop rent", "Electricity", "Transport", "Tea/food", "Parts", "Other")

@Composable
fun ExpensesScreen(viewModel: ShopViewModel) {
    val expenses by viewModel.expenses.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    var showAddEditDialog by remember { mutableStateOf(false) }
    var selectedExpenseForEdit by remember { mutableStateOf<Expense?>(null) }
    var expenseToDelete by remember { mutableStateOf<Expense?>(null) }

    val filteredExpenses = remember(expenses, searchQuery, selectedCategory) {
        expenses.filter { exp ->
            val matchesCategory = (selectedCategory == "All" || exp.category.equals(selectedCategory, ignoreCase = true))
            val matchesQuery = searchQuery.isBlank() || exp.name.contains(searchQuery, ignoreCase = true) || exp.description.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }

    val totalExpenseAmount = expenses.sumOf { it.amount }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedExpenseForEdit = null
                    showAddEditDialog = true
                },
                containerColor = GoldPrimary,
                contentColor = Color.Black
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Expense")
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
            // Header summary
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(14.dp).fillMaxWidth()
                ) {
                    Column {
                        Text("📉 Shop Expenses Tracker", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                        Text("${expenses.size} Total Expenses Recorded", fontSize = 12.sp, color = Color.White)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Total Amount", fontSize = 11.sp, color = Color.Gray)
                        Text("Rs. ${totalExpenseAmount.toInt()}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE57373))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search expense name...") },
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

            // Category Filter Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ExpenseCategories.forEach { category ->
                    val isSelected = category == selectedCategory
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = { Text(category) },
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

            // Expenses List
            if (filteredExpenses.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth().weight(1f)
                ) {
                    Text("No expenses recorded.", color = Color.Gray, fontSize = 14.sp)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredExpenses, key = { it.id }) { expense ->
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
                                        Text(expense.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                                        Text("Category: ${expense.category} • Date: ${expense.date}", fontSize = 12.sp, color = Color.LightGray)
                                    }
                                    Text("Rs. ${expense.amount.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE57373))
                                }

                                if (expense.description.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Note: ${expense.description}", fontSize = 11.sp, color = Color.Gray)
                                }

                                Divider(color = DarkBorder, modifier = Modifier.padding(vertical = 8.dp))

                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    IconButton(
                                        onClick = {
                                            selectedExpenseForEdit = expense
                                            showAddEditDialog = true
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = GoldPrimary, modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(
                                        onClick = { expenseToDelete = expense },
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
        AddEditExpenseDialog(
            expense = selectedExpenseForEdit,
            onDismiss = { showAddEditDialog = false },
            onSave = { exp ->
                if (selectedExpenseForEdit == null) {
                    viewModel.addExpense(exp)
                } else {
                    viewModel.updateExpense(exp)
                }
                showAddEditDialog = false
            }
        )
    }

    // Delete confirmation
    if (expenseToDelete != null) {
        AlertDialog(
            onDismissRequest = { expenseToDelete = null },
            title = { Text("Delete Expense", color = GoldPrimary) },
            text = { Text("Are you sure you want to delete expense '${expenseToDelete?.name}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        expenseToDelete?.let { viewModel.deleteExpense(it.id) }
                        expenseToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE57373))
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { expenseToDelete = null }) {
                    Text("Cancel", color = Color.White)
                }
            },
            containerColor = DarkSurface
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditExpenseDialog(
    expense: Expense?,
    onDismiss: () -> Unit,
    onSave: (Expense) -> Unit
) {
    var name by remember { mutableStateOf(expense?.name ?: "") }
    var category by remember { mutableStateOf(expense?.category ?: "Tea/food") }
    var amountStr by remember { mutableStateOf(expense?.amount?.let { if (it == 0.0) "" else it.toString() } ?: "") }
    var description by remember { mutableStateOf(expense?.description ?: "") }

    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (expense == null) "Add Expense" else "Edit Expense", color = GoldPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Expense Name (e.g. Shop Rent / Electricity)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = categoryDropdownExpanded,
                    onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false }
                    ) {
                        ExpenseCategories.filter { it != "All" }.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
                                    categoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Amount (Rs)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description / Notes") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val expObj = Expense(
                            id = expense?.id ?: 0,
                            name = name.trim(),
                            category = category,
                            amount = amountStr.toDoubleOrNull() ?: 0.0,
                            date = expense?.date ?: ShopViewModel.getTodayDateString(),
                            description = description.trim()
                        )
                        onSave(expObj)
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
