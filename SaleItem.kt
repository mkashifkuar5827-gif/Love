package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sale_items")
data class SaleItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val invoiceId: Long,
    val itemType: String, // "Mobile" or "Accessory"
    val itemId: Long? = null,
    val description: String,
    val unitPrice: Double,
    val quantity: Int,
    val subtotal: Double
)
