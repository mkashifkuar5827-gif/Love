package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.entity.SaleInvoice
import com.example.data.entity.SaleItem
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GoldPrimary

@Composable
fun ReceiptDialog(
    invoice: SaleInvoice,
    items: List<SaleItem>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val receiptText = buildString {
        appendLine("===============================")
        appendLine("    KASHIF MOBILE AND REPAIR   ")
        appendLine(" Offline Mobile & Repair Shop ")
        appendLine("===============================")
        appendLine("Invoice No: ${invoice.invoiceNumber}")
        appendLine("Date: ${invoice.date}")
        appendLine("Customer: ${invoice.customerName}")
        if (invoice.customerPhone.isNotBlank()) {
            appendLine("Phone: ${invoice.customerPhone}")
        }
        appendLine("-------------------------------")
        appendLine(String.format("%-18s %3s %8s", "Product", "Qty", "Price"))
        appendLine("-------------------------------")
        items.forEach { item ->
            appendLine(String.format("%-18s %3d %8.0f", item.description.take(18), item.quantity, item.subtotal))
        }
        appendLine("-------------------------------")
        appendLine("Subtotal:  Rs. ${invoice.totalAmount}")
        appendLine("Discount:  Rs. ${invoice.discount}")
        appendLine("TOTAL:     Rs. ${invoice.finalTotal}")
        appendLine("Paid:      Rs. ${invoice.paidAmount}")
        appendLine("Remaining: Rs. ${invoice.remainingBalance}")
        appendLine("===============================")
        appendLine("   Thank You For Your Visit!   ")
        appendLine("===============================")
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .border(1.dp, GoldPrimary, RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Receipt Preview",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Divider(color = GoldPrimary.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 8.dp))

                // Thermal receipt style display
                Surface(
                    color = Color(0xFF181818),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 340.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        item {
                            Text(
                                text = "KASHIF MOBILE AND REPAIR",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldPrimary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = "Invoice #: ${invoice.invoiceNumber}",
                                fontSize = 12.sp,
                                color = Color.LightGray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = "Date: ${invoice.date}",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = "Customer: ${invoice.customerName} ${if (invoice.customerPhone.isNotBlank()) "(${invoice.customerPhone})" else ""}",
                                fontSize = 12.sp,
                                color = Color.White,
                                modifier = Modifier.padding(top = 6.dp, bottom = 6.dp)
                            )
                            Divider(color = Color.DarkGray)
                        }

                        items(items) { item ->
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.description,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "${item.quantity} x Rs. ${item.unitPrice}",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }
                                Text(
                                    text = "Rs. ${item.subtotal}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldPrimary
                                )
                            }
                        }

                        item {
                            Divider(color = Color.DarkGray, modifier = Modifier.padding(vertical = 6.dp))
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Subtotal:", fontSize = 12.sp, color = Color.LightGray)
                                Text("Rs. ${invoice.totalAmount}", fontSize = 12.sp, color = Color.LightGray)
                            }
                            if (invoice.discount > 0) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Discount:", fontSize = 12.sp, color = Color.LightGray)
                                    Text("- Rs. ${invoice.discount}", fontSize = 12.sp, color = Color(0xFFFF8A80))
                                }
                            }
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                            ) {
                                Text("Total:", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                                Text("Rs. ${invoice.finalTotal}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                            }
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Paid Amount:", fontSize = 13.sp, color = Color.White)
                                Text("Rs. ${invoice.paidAmount}", fontSize = 13.sp, color = Color.White)
                            }
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Remaining:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (invoice.remainingBalance > 0) Color(0xFFFFB74D) else Color.Gray)
                                Text("Rs. ${invoice.remainingBalance}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (invoice.remainingBalance > 0) Color(0xFFFFB74D) else Color.Gray)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(receiptText))
                            Toast.makeText(context, "Receipt copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldPrimary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, receiptText)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Share Receipt")
                            context.startActivity(shareIntent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
