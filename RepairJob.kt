package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repair_jobs")
data class RepairJob(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val customerName: String,
    val customerPhone: String,
    val brand: String,
    val model: String,
    val imei: String = "",
    val problem: String,
    val repairDetails: String = "",
    val cost: Double,
    val advancePayment: Double = 0.0,
    val date: String, // Job created/registered date YYYY-MM-DD
    val expectedDeliveryDate: String = "",
    val status: String = "Pending", // Pending, Repairing, Completed, Delivered, Cancelled
    val notes: String = ""
) {
    val remainingPayment: Double
        get() = (cost - advancePayment).coerceAtLeast(0.0)
}
