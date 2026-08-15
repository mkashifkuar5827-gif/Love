package com.example.data.entity

data class BackupData(
    val version: Int = 1,
    val backupDate: String,
    val mobiles: List<MobileStock> = emptyList(),
    val accessories: List<Accessory> = emptyList(),
    val repairJobs: List<RepairJob> = emptyList(),
    val customers: List<Customer> = emptyList(),
    val saleInvoices: List<SaleInvoice> = emptyList(),
    val saleItems: List<SaleItem> = emptyList(),
    val expenses: List<Expense> = emptyList()
)
