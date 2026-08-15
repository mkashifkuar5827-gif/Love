package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mobile_stock")
data class MobileStock(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val brand: String,
    val model: String,
    val ram: String,
    val storage: String,
    val color: String,
    val imei: String,
    val purchasePrice: Double,
    val salePrice: Double,
    val quantity: Int,
    val date: String,
    val notes: String = ""
)
