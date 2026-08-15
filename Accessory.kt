package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accessories")
data class Accessory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String, // Charger, Handsfree, Mobile Cover, Glass Protector, Battery, Cable, Power Bank, Earphones, Other
    val purchasePrice: Double,
    val salePrice: Double,
    val quantity: Int,
    val date: String,
    val notes: String = ""
)
