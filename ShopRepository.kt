package com.example.data.repository

import com.example.data.dao.ShopDao
import com.example.data.entity.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class ShopRepository(private val dao: ShopDao) {

    // Mobiles
    val allMobiles: Flow<List<MobileStock>> = dao.getAllMobiles()
    fun searchMobiles(query: String): Flow<List<MobileStock>> = dao.searchMobiles(query)
    suspend fun insertMobile(mobile: MobileStock): Long = dao.insertMobile(mobile)
    suspend fun updateMobile(mobile: MobileStock) = dao.updateMobile(mobile)
    suspend fun deleteMobile(id: Long) = dao.deleteMobile(id)
    suspend fun updateMobileQuantity(id: Long, quantity: Int) = dao.updateMobileQuantity(id, quantity)

    // Accessories
    val allAccessories: Flow<List<Accessory>> = dao.getAllAccessories()
    fun searchAccessories(query: String): Flow<List<Accessory>> = dao.searchAccessories(query)
    suspend fun insertAccessory(accessory: Accessory): Long = dao.insertAccessory(accessory)
    suspend fun updateAccessory(accessory: Accessory) = dao.updateAccessory(accessory)
    suspend fun deleteAccessory(id: Long) = dao.deleteAccessory(id)
    suspend fun updateAccessoryQuantity(id: Long, quantity: Int) = dao.updateAccessoryQuantity(id, quantity)

    // Repair Jobs
    val allRepairJobs: Flow<List<RepairJob>> = dao.getAllRepairJobs()
    fun getRepairJobsByStatus(status: String): Flow<List<RepairJob>> = dao.getRepairJobsByStatus(status)
    fun getRepairJobsByDate(date: String): Flow<List<RepairJob>> = dao.getRepairJobsByDate(date)
    fun searchRepairJobs(query: String): Flow<List<RepairJob>> = dao.searchRepairJobs(query)
    suspend fun insertRepairJob(job: RepairJob): Long = dao.insertRepairJob(job)
    suspend fun updateRepairJob(job: RepairJob) = dao.updateRepairJob(job)
    suspend fun deleteRepairJob(id: Long) = dao.deleteRepairJob(id)

    // Customers
    val allCustomers: Flow<List<Customer>> = dao.getAllCustomers()
    fun searchCustomers(query: String): Flow<List<Customer>> = dao.searchCustomers(query)
    suspend fun insertCustomer(customer: Customer): Long = dao.insertCustomer(customer)
    suspend fun updateCustomer(customer: Customer) = dao.updateCustomer(customer)
    suspend fun deleteCustomer(id: Long) = dao.deleteCustomer(id)

    // Sales & Invoices
    val allInvoices: Flow<List<SaleInvoice>> = dao.getAllInvoices()
    val allSaleItems: Flow<List<SaleItem>> = dao.getAllSaleItems()
    fun getInvoicesByDate(date: String): Flow<List<SaleInvoice>> = dao.getInvoicesByDate(date)
    fun getInvoiceItems(invoiceId: Long): Flow<List<SaleItem>> = dao.getInvoiceItems(invoiceId)
    fun searchInvoices(query: String): Flow<List<SaleInvoice>> = dao.searchInvoices(query)
    suspend fun deleteInvoice(id: Long) {
        dao.deleteSaleItemsForInvoice(id)
        dao.deleteInvoice(id)
    }

    // Perform a sale: insert invoice, insert items, reduce stock
    suspend fun processSale(
        invoice: SaleInvoice,
        items: List<SaleItem>
    ): Long {
        val invoiceId = dao.insertInvoice(invoice)
        for (item in items) {
            val itemWithInvoice = item.copy(invoiceId = invoiceId)
            dao.insertSaleItem(itemWithInvoice)

            // Reduce stock automatically
            if (item.itemId != null) {
                if (item.itemType == "Mobile") {
                    val mobile = dao.getMobileById(item.itemId)
                    if (mobile != null) {
                        val newQty = (mobile.quantity - item.quantity).coerceAtLeast(0)
                        dao.updateMobileQuantity(mobile.id, newQty)
                    }
                } else if (item.itemType == "Accessory") {
                    val acc = dao.getAccessoryById(item.itemId)
                    if (acc != null) {
                        val newQty = (acc.quantity - item.quantity).coerceAtLeast(0)
                        dao.updateAccessoryQuantity(acc.id, newQty)
                    }
                }
            }
        }
        return invoiceId
    }

    // Expenses
    val allExpenses: Flow<List<Expense>> = dao.getAllExpenses()
    fun getExpensesByDate(date: String): Flow<List<Expense>> = dao.getExpensesByDate(date)
    suspend fun insertExpense(expense: Expense): Long = dao.insertExpense(expense)
    suspend fun updateExpense(expense: Expense) = dao.updateExpense(expense)
    suspend fun deleteExpense(id: Long) = dao.deleteExpense(id)

    // Backup and Restore (JSON)
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    suspend fun exportBackupJson(backupDate: String): String {
        val mobiles = dao.getAllMobiles().first()
        val accessories = dao.getAllAccessories().first()
        val repairs = dao.getAllRepairJobs().first()
        val customers = dao.getAllCustomers().first()
        val invoices = dao.getAllInvoices().first()
        val items = dao.getAllSaleItems().first()
        val expenses = dao.getAllExpenses().first()

        val backupData = BackupData(
            version = 1,
            backupDate = backupDate,
            mobiles = mobiles,
            accessories = accessories,
            repairJobs = repairs,
            customers = customers,
            saleInvoices = invoices,
            saleItems = items,
            expenses = expenses
        )

        val adapter = moshi.adapter(BackupData::class.java)
        return adapter.toJson(backupData)
    }

    suspend fun importBackupJson(jsonString: String): Boolean {
        return try {
            val adapter = moshi.adapter(BackupData::class.java)
            val backupData = adapter.fromJson(jsonString) ?: return false

            // Clear database
            dao.deleteAllMobiles()
            dao.deleteAllAccessories()
            dao.deleteAllRepairJobs()
            dao.deleteAllCustomers()
            dao.deleteAllInvoices()
            dao.deleteAllSaleItems()
            dao.deleteAllExpenses()

            // Re-populate
            backupData.mobiles.forEach { dao.insertMobile(it) }
            backupData.accessories.forEach { dao.insertAccessory(it) }
            backupData.repairJobs.forEach { dao.insertRepairJob(it) }
            backupData.customers.forEach { dao.insertCustomer(it) }
            backupData.saleInvoices.forEach { dao.insertInvoice(it) }
            backupData.saleItems.forEach { dao.insertSaleItem(it) }
            backupData.expenses.forEach { dao.insertExpense(it) }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
