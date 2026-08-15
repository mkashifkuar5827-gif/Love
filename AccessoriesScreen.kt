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
import com.example.data.entity.Accessory
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GoldPrimary
import com.example.ui.viewmodel.ShopViewModel

val AccessoryCategories = listOf(
    "All",
    "Charger",
    "Handsfree",
    "Mobile Cover",
    "Glass Protector",
    "Battery",
    "Cable",
    "Power Bank",
    "Earphones",
    "Other"
)

@Composable
fun AccessoriesScreen(viewModel: ShopViewModel) {
    val accessories by viewModel.accessories.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    var showAddEditDialog by remember { mutableStateOf(false) }
    var selectedAccessoryForEdit by remember { mutableStateOf<Accessory?>(null) }
    var accessoryToDelete by remember { mutableStateOf<Accessory?>(null) }

    val filteredAccessories = remember(accessories, searchQuery, selectedCategory) {
        accessories.filter { acc ->
            val matchesCategory = (selectedCategory == "All" || acc.category.equals(selectedCategory, ignoreCase = true))
            val matchesQuery = searchQuery.isBlank() || acc.name.contains(searchQuery, ignoreCase = true) || acc.category.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }

    val totalItems = accessories.sumOf { it.quantity }
    val totalStockValue = accessories.sumOf { it.purchasePrice * it.quantity }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedAccessoryForEdit = null
                    showAddEditDialog = true
                },
                containerColor = GoldPrimary,
                contentColor = Color.Black
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Accessory")
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
                        Text("🎧 Accessories Stock", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                        Text("$totalItems Items in stock", fontSize = 12.sp, color = Color.White)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Stock Value", fontSize = 11.sp, color = Color.Gray)
                        Text("Rs. ${totalStockValue.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search accessory name...") },
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
                AccessoryCategories.forEach { category ->
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

            // List of Accessories
            if (filteredAccessories.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Text(
                        text = "No accessories found.",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredAccessories, key = { it.id }) { acc ->
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
                                        Text(
                                            text = acc.name,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldPrimary
                                        )
                                        Text(
                                            text = "Category: ${acc.category}",
                                            fontSize = 12.sp,
                                            color = Color.LightGray
                                        )
                                    }

                                    // Quick Quantity Adjuster
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .background(DarkSurfaceVariant, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        IconButton(
                                            onClick = { viewModel.updateAccessoryQty(acc, -1) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease", tint = GoldPrimary, modifier = Modifier.size(16.dp))
                                        }
                                        Text(
                                            text = "${acc.quantity}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 6.dp)
                                        )
                                        IconButton(
                                            onClick = { viewModel.updateAccessoryQty(acc, 1) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Add, contentDescription = "Increase", tint = GoldPrimary, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }

                                Divider(color = DarkBorder, modifier = Modifier.padding(vertical = 8.dp))

                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Buy: Rs. ${acc.purchasePrice.toInt()} | Sell: Rs. ${acc.salePrice.toInt()}", fontSize = 12.sp, color = Color.LightGray)

                                    Row {
                                        IconButton(
                                            onClick = {
                                                selectedAccessoryForEdit = acc
                                                showAddEditDialog = true
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = GoldPrimary, modifier = Modifier.size(18.dp))
                                        }
                                        IconButton(
                                            onClick = { accessoryToDelete = acc },
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

    // Add / Edit Dialog
    if (showAddEditDialog) {
        AddEditAccessoryDialog(
            accessory = selectedAccessoryForEdit,
            onDismiss = { showAddEditDialog = false },
            onSave = { acc ->
                if (selectedAccessoryForEdit == null) {
                    viewModel.addAccessory(acc)
                } else {
                    viewModel.updateAccessory(acc)
                }
                showAddEditDialog = false
            }
        )
    }

    // Delete confirmation
    if (accessoryToDelete != null) {
        AlertDialog(
            onDismissRequest = { accessoryToDelete = null },
            title = { Text("Delete Accessory", color = GoldPrimary) },
            text = { Text("Are you sure you want to delete ${accessoryToDelete?.name}?") },
            confirmButton = {
                Button(
                    onClick = {
                        accessoryToDelete?.let { viewModel.deleteAccessory(it.id) }
                        accessoryToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE57373))
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { accessoryToDelete = null }) {
                    Text("Cancel", color = Color.White)
                }
            },
            containerColor = DarkSurface
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAccessoryDialog(
    accessory: Accessory?,
    onDismiss: () -> Unit,
    onSave: (Accessory) -> Unit
) {
    var name by remember { mutableStateOf(accessory?.name ?: "") }
    var category by remember { mutableStateOf(accessory?.category ?: "Charger") }
    var purchasePrice by remember { mutableStateOf(accessory?.purchasePrice?.let { if (it == 0.0) "" else it.toString() } ?: "") }
    var salePrice by remember { mutableStateOf(accessory?.salePrice?.let { if (it == 0.0) "" else it.toString() } ?: "") }
    var quantity by remember { mutableStateOf(accessory?.quantity?.toString() ?: "1") }
    var notes by remember { mutableStateOf(accessory?.notes ?: "") }

    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (accessory == null) "Add Accessory" else "Edit Accessory", color = GoldPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Accessory Name (e.g. Fast Charger 25W)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Category Dropdown
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
                        AccessoryCategories.filter { it != "All" }.forEach { cat ->
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

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = purchasePrice,
                        onValueChange = { purchasePrice = it },
                        label = { Text("Buy Price") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = salePrice,
                        onValueChange = { salePrice = it },
                        label = { Text("Sell Price") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantity") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val accObj = Accessory(
                            id = accessory?.id ?: 0,
                            name = name.trim(),
                            category = category,
                            purchasePrice = purchasePrice.toDoubleOrNull() ?: 0.0,
                            salePrice = salePrice.toDoubleOrNull() ?: 0.0,
                            quantity = quantity.toIntOrNull() ?: 1,
                            date = accessory?.date ?: ShopViewModel.getTodayDateString(),
                            notes = notes.trim()
                        )
                        onSave(accObj)
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
