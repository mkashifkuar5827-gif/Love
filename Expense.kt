package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String, // Shop rent, Electricity, Transport, Tea/food, Parts, Other
    val amount: Double,
    val date: String, // YYYY-MM-DD
    val description: String = ""
)
