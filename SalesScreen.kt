package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ReceiptDialog
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GoldPrimary
import com.example.ui.viewmodel.CartItem
import com.example.ui.viewmodel.ShopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesScreen(viewModel: ShopViewModel) {
    val context = LocalContext.current
    val cartItems by viewModel.cartItems.collectAsState()
    val mobiles by viewModel.mobiles.collectAsState()
    val accessories by viewModel.accessories.collectAsState()
    val customers by viewModel.customers.collectAsState()

    val lastGeneratedInvoice by viewModel.lastGeneratedInvoice.collectAsState()
    val lastGeneratedItems by viewModel.lastGeneratedItems.collectAsState()

    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var discountStr by remember { mutableStateOf("") }
    var paidAmountStr by remember { mutableStateOf("") }

    var showProductPickerModal by remember { mutableStateOf(false) }

    val rawSubtotal = cartItems.sumOf { it.subtotal }
    val discountVal = discountStr.toDoubleOrNull() ?: 0.0
    val finalTotal = (rawSubtotal - discountVal).coerceAtLeast(0.0)
    val paidVal = paidAmountStr.toDoubleOrNull() ?: finalTotal
    val remainingVal = (finalTotal - paidVal).coerceAtLeast(0.0)

    Scaffold(
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Header
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text("🛒 Point Of Sale", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                    Text("Create new sale invoice", fontSize = 12.sp, color = Color.Gray)
                }

                Button(
                    onClick = { showProductPickerModal = true },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Item")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Customer details inputs
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Customer Information", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = customerName,
                            onValueChange = { customerName = it },
                            label = { Text("Customer Name") },
                            placeholder = { Text("Walk-in Customer") },
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
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Cart Items List
            Text("Invoice Products (${cartItems.size})", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(6.dp))

            if (cartItems.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(DarkSurface, RoundedCornerShape(12.dp))
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Cart is empty. Tap 'Add Item' to select stock.", color = Color.Gray, fontSize = 13.sp)
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    itemsIndexed(cartItems) { index, item ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(10.dp).fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text("Rs. ${item.unitPrice.toInt()} each • Max: ${item.maxAvailable}", fontSize = 11.sp, color = Color.Gray)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { viewModel.updateCartItemQuantity(index, item.quantity - 1) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Remove, contentDescription = "-", tint = GoldPrimary, modifier = Modifier.size(16.dp))
                                    }
                                    Text("${item.quantity}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GoldPrimary, modifier = Modifier.padding(horizontal = 4.dp))
                                    IconButton(
                                        onClick = { viewModel.updateCartItemQuantity(index, item.quantity + 1) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Add, contentDescription = "+", tint = GoldPrimary, modifier = Modifier.size(16.dp))
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Rs. ${item.subtotal.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)

                                    IconButton(
                                        onClick = { viewModel.removeFromCart(index) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Close, contentDescription = "Remove", tint = Color(0xFFE57373), modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Payment Calculations Card
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GoldPrimary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Subtotal:", fontSize = 13.sp, color = Color.Gray)
                        Text("Rs. ${rawSubtotal.toInt()}", fontSize = 13.sp, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = discountStr,
                            onValueChange = { discountStr = it },
                            label = { Text("Discount (Rs)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = paidAmountStr,
                            onValueChange = { paidAmountStr = it },
                            label = { Text("Paid Amount (Rs)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(color = DarkBorder)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("FINAL TOTAL:", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                        Text("Rs. ${finalTotal.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
                    ) {
                        Text("Remaining Balance:", fontSize = 13.sp, color = if (remainingVal > 0) Color(0xFFFFB74D) else Color.Gray)
                        Text("Rs. ${remainingVal.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (remainingVal > 0) Color(0xFFFFB74D) else Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            if (cartItems.isEmpty()) {
                                Toast.makeText(context, "Please add products to cart first!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            viewModel.completeSale(
                                customerName = customerName,
                                customerPhone = customerPhone,
                                discount = discountVal,
                                paidAmount = paidVal,
                                onComplete = {
                                    customerName = ""
                                    customerPhone = ""
                                    discountStr = ""
                                    paidAmountStr = ""
                                    Toast.makeText(context, "Sale completed & stock updated!", Toast.LENGTH_SHORT).show()
                                }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Receipt, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("COMPLETE SALE & GENERATE RECEIPT", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }

    // Modal to pick stock items (Mobiles or Accessories)
    if (showProductPickerModal) {
        ProductPickerModal(
            mobiles = mobiles.filter { it.quantity > 0 },
            accessories = accessories.filter { it.quantity > 0 },
            onDismiss = { showProductPickerModal = false },
            onSelectItem = { item ->
                viewModel.addToCart(item)
                showProductPickerModal = false
            }
        )
    }

    // Receipt Dialog Preview when invoice is generated
    if (lastGeneratedInvoice != null) {
        ReceiptDialog(
            invoice = lastGeneratedInvoice!!,
            items = lastGeneratedItems,
            onDismiss = { viewModel.clearLastInvoice() }
        )
    }
}

@Composable
fun ProductPickerModal(
    mobiles: List<com.example.data.entity.MobileStock>,
    accessories: List<com.example.data.entity.Accessory>,
    onDismiss: () -> Unit,
    onSelectItem: (CartItem) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Mobiles, 1 = Accessories
    var pickerSearchQuery by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Product To Sell", color = GoldPrimary) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = DarkSurfaceVariant,
                    contentColor = GoldPrimary
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Mobiles (${mobiles.size})", fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Accessories (${accessories.size})", fontSize = 12.sp) }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = pickerSearchQuery,
                    onValueChange = { pickerSearchQuery = it },
                    placeholder = { Text("Search product name...") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = GoldPrimary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.height(280.dp)
                ) {
                    if (selectedTab == 0) {
                        val filteredMobiles = mobiles.filter {
                            pickerSearchQuery.isBlank() ||
                                    it.brand.contains(pickerSearchQuery, ignoreCase = true) ||
                                    it.model.contains(pickerSearchQuery, ignoreCase = true)
                        }
                        items(filteredMobiles.size) { idx ->
                            val mob = filteredMobiles[idx]
                            Surface(
                                color = DarkSurfaceVariant,
                                shape = RoundedCornerShape(8.dp),
                                onClick = {
                                    onSelectItem(
                                        CartItem(
                                            itemType = "Mobile",
                                            itemId = mob.id,
                                            name = "${mob.brand} ${mob.model} (${mob.color})",
                                            unitPrice = mob.salePrice,
                                            quantity = 1,
                                            maxAvailable = mob.quantity
                                        )
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(10.dp).fillMaxWidth()
                                ) {
                                    Column {
                                        Text("${mob.brand} ${mob.model}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        Text("Stock Qty: ${mob.quantity} • ${mob.ram}/${mob.storage}", fontSize = 11.sp, color = Color.Gray)
                                    }
                                    Text("Rs. ${mob.salePrice.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                                }
                            }
                        }
                    } else {
                        val filteredAccessories = accessories.filter {
                            pickerSearchQuery.isBlank() ||
                                    it.name.contains(pickerSearchQuery, ignoreCase = true) ||
                                    it.category.contains(pickerSearchQuery, ignoreCase = true)
                        }
                        items(filteredAccessories.size) { idx ->
                            val acc = filteredAccessories[idx]
                            Surface(
                                color = DarkSurfaceVariant,
                                shape = RoundedCornerShape(8.dp),
                                onClick = {
                                    onSelectItem(
                                        CartItem(
                                            itemType = "Accessory",
                                            itemId = acc.id,
                                            name = "${acc.name} (${acc.category})",
                                            unitPrice = acc.salePrice,
                                            quantity = 1,
                                            maxAvailable = acc.quantity
                                        )
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(10.dp).fillMaxWidth()
                                ) {
                                    Column {
                                        Text(acc.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        Text("Stock Qty: ${acc.quantity} • ${acc.category}", fontSize = 11.sp, color = Color.Gray)
                                    }
                                    Text("Rs. ${acc.salePrice.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.White)
            }
        },
        containerColor = DarkSurface
    )
}
