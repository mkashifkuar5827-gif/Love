package com.example.ui.screens

import androidx.compose.foundation.background
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
import com.example.data.entity.MobileStock
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GoldPrimary
import com.example.ui.viewmodel.ShopViewModel

@Composable
fun MobilesScreen(viewModel: ShopViewModel) {
    val mobiles by viewModel.mobiles.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    var showAddEditDialog by remember { mutableStateOf(false) }
    var selectedMobileForEdit by remember { mutableStateOf<MobileStock?>(null) }
    var mobileToDelete by remember { mutableStateOf<MobileStock?>(null) }

    val filteredMobiles = remember(mobiles, searchQuery) {
        if (searchQuery.isBlank()) mobiles
        else mobiles.filter {
            it.brand.contains(searchQuery, ignoreCase = true) ||
                    it.model.contains(searchQuery, ignoreCase = true) ||
                    it.imei.contains(searchQuery, ignoreCase = true) ||
                    it.color.contains(searchQuery, ignoreCase = true)
        }
    }

    // Auto Summary Totals
    val totalMobilesInStock = mobiles.size
    val totalPurchaseValue = mobiles.sumOf { it.purchasePrice * it.quantity }
    val totalExpectedSaleValue = mobiles.sumOf { it.salePrice * it.quantity }
    val totalAvailableQuantity = mobiles.sumOf { it.quantity }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedMobileForEdit = null
                    showAddEditDialog = true
                },
                containerColor = GoldPrimary,
                contentColor = Color.Black
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Mobile")
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
            // Auto Summary Header Card
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "📱 Mobile Stock Overview",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text("Total Models", fontSize = 11.sp, color = Color.Gray)
                            Text("$totalMobilesInStock Models", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Column {
                            Text("Available Qty", fontSize = 11.sp, color = Color.Gray)
                            Text("$totalAvailableQuantity Units", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text("Purchase Value", fontSize = 11.sp, color = Color.Gray)
                            Text("Rs. ${totalPurchaseValue.toInt()}", fontSize = 13.sp, color = Color.LightGray)
                        }
                        Column {
                            Text("Expected Sale", fontSize = 11.sp, color = Color.Gray)
                            Text("Rs. ${totalExpectedSaleValue.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF81C784))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search brand, model, IMEI, color...") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = GoldPrimary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
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

            // List of Mobile Stock items
            if (filteredMobiles.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Text(
                        text = if (searchQuery.isEmpty()) "No mobile stock added yet.\nTap '+' to add your first mobile!" else "No mobiles found matching '$searchQuery'.",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredMobiles, key = { it.id }) { mobile ->
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
                                            text = "${mobile.brand} ${mobile.model}",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldPrimary
                                        )
                                        Text(
                                            text = "${mobile.ram} RAM / ${mobile.storage} Storage • ${mobile.color}",
                                            fontSize = 12.sp,
                                            color = Color.White
                                        )
                                        if (mobile.imei.isNotBlank()) {
                                            Text(
                                                text = "IMEI: ${mobile.imei}",
                                                fontSize = 11.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    }

                                    // Quick Quantity Adjuster
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .background(DarkSurfaceVariant, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        IconButton(
                                            onClick = { viewModel.updateMobileQty(mobile, -1) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease", tint = GoldPrimary, modifier = Modifier.size(16.dp))
                                        }
                                        Text(
                                            text = "${mobile.quantity}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 6.dp)
                                        )
                                        IconButton(
                                            onClick = { viewModel.updateMobileQty(mobile, 1) },
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
                                    Column {
                                        Text("Buy: Rs. ${mobile.purchasePrice.toInt()} | Sell: Rs. ${mobile.salePrice.toInt()}", fontSize = 12.sp, color = Color.LightGray)
                                        if (mobile.notes.isNotBlank()) {
                                            Text("Note: ${mobile.notes}", fontSize = 11.sp, color = Color.Gray)
                                        }
                                    }

                                    Row {
                                        IconButton(
                                            onClick = {
                                                selectedMobileForEdit = mobile
                                                showAddEditDialog = true
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = GoldPrimary, modifier = Modifier.size(18.dp))
                                        }
                                        IconButton(
                                            onClick = { mobileToDelete = mobile },
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

    // Add / Edit Mobile Dialog
    if (showAddEditDialog) {
        AddEditMobileDialog(
            mobile = selectedMobileForEdit,
            onDismiss = { showAddEditDialog = false },
            onSave = { mobile ->
                if (selectedMobileForEdit == null) {
                    viewModel.addMobile(mobile)
                } else {
                    viewModel.updateMobile(mobile)
                }
                showAddEditDialog = false
            }
        )
    }

    // Confirm Delete Dialog
    if (mobileToDelete != null) {
        AlertDialog(
            onDismissRequest = { mobileToDelete = null },
            title = { Text("Delete Mobile", color = GoldPrimary) },
            text = { Text("Are you sure you want to delete ${mobileToDelete?.brand} ${mobileToDelete?.model}?") },
            confirmButton = {
                Button(
                    onClick = {
                        mobileToDelete?.let { viewModel.deleteMobile(it.id) }
                        mobileToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE57373))
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { mobileToDelete = null }) {
                    Text("Cancel", color = Color.White)
                }
            },
            containerColor = DarkSurface
        )
    }
}

@Composable
fun AddEditMobileDialog(
    mobile: MobileStock?,
    onDismiss: () -> Unit,
    onSave: (MobileStock) -> Unit
) {
    var brand by remember { mutableStateOf(mobile?.brand ?: "") }
    var model by remember { mutableStateOf(mobile?.model ?: "") }
    var ram by remember { mutableStateOf(mobile?.ram ?: "") }
    var storage by remember { mutableStateOf(mobile?.storage ?: "") }
    var color by remember { mutableStateOf(mobile?.color ?: "") }
    var imei by remember { mutableStateOf(mobile?.imei ?: "") }
    var purchasePrice by remember { mutableStateOf(mobile?.purchasePrice?.let { if (it == 0.0) "" else it.toString() } ?: "") }
    var salePrice by remember { mutableStateOf(mobile?.salePrice?.let { if (it == 0.0) "" else it.toString() } ?: "") }
    var quantity by remember { mutableStateOf(mobile?.quantity?.toString() ?: "1") }
    var notes by remember { mutableStateOf(mobile?.notes ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (mobile == null) "Add New Mobile" else "Edit Mobile Stock", color = GoldPrimary) },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    OutlinedTextField(
                        value = brand,
                        onValueChange = { brand = it },
                        label = { Text("Brand (e.g. Samsung, Vivo, iPhone)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = model,
                        onValueChange = { model = it },
                        label = { Text("Model (e.g. Galaxy S23, A12, Y20)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = ram,
                            onValueChange = { ram = it },
                            label = { Text("RAM (e.g. 4GB, 8GB)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = storage,
                            onValueChange = { storage = it },
                            label = { Text("Storage (128GB, 256GB)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = color,
                            onValueChange = { color = it },
                            label = { Text("Color") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = quantity,
                            onValueChange = { quantity = it },
                            label = { Text("Quantity") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = imei,
                        onValueChange = { imei = it },
                        label = { Text("IMEI Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = purchasePrice,
                            onValueChange = { purchasePrice = it },
                            label = { Text("Purchase Price") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = salePrice,
                            onValueChange = { salePrice = it },
                            label = { Text("Sale Price") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes (Optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (brand.isNotBlank() && model.isNotBlank()) {
                        val mobileObj = MobileStock(
                            id = mobile?.id ?: 0,
                            brand = brand.trim(),
                            model = model.trim(),
                            ram = ram.trim(),
                            storage = storage.trim(),
                            color = color.trim(),
                            imei = imei.trim(),
                            purchasePrice = purchasePrice.toDoubleOrNull() ?: 0.0,
                            salePrice = salePrice.toDoubleOrNull() ?: 0.0,
                            quantity = quantity.toIntOrNull() ?: 1,
                            date = mobile?.date ?: ShopViewModel.getTodayDateString(),
                            notes = notes.trim()
                        )
                        onSave(mobileObj)
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
