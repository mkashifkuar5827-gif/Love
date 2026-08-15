package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sale_invoices")
data class SaleInvoice(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val invoiceNumber: String,
    val customerName: String,
    val customerPhone: String = "",
    val customerId: Long? = null,
    val date: String, // YYYY-MM-DD
    val totalAmount: Double,
    val discount: Double = 0.0,
    val finalTotal: Double,
    val paidAmount: Double,
    val remainingBalance: Double,
    val notes: String = ""
)
