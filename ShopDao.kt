package com.example.data.dao

import androidx.room.*
import com.example.data.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopDao {

    // --- Mobile Stock ---
    @Query("SELECT * FROM mobile_stock ORDER BY id DESC")
    fun getAllMobiles(): Flow<List<MobileStock>>

    @Query("SELECT * FROM mobile_stock WHERE id = :id")
    suspend fun getMobileById(id: Long): MobileStock?

    @Query("""
        SELECT * FROM mobile_stock 
        WHERE brand LIKE '%' || :query || '%' 
        OR model LIKE '%' || :query || '%' 
        OR imei LIKE '%' || :query || '%'
        OR color LIKE '%' || :query || '%'
        ORDER BY id DESC
    """)
    fun searchMobiles(query: String): Flow<List<MobileStock>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMobile(mobile: MobileStock): Long

    @Update
    suspend fun updateMobile(mobile: MobileStock)

    @Query("DELETE FROM mobile_stock WHERE id = :id")
    suspend fun deleteMobile(id: Long)

    @Query("UPDATE mobile_stock SET quantity = :quantity WHERE id = :id")
    suspend fun updateMobileQuantity(id: Long, quantity: Int)

    // --- Accessories ---
    @Query("SELECT * FROM accessories ORDER BY id DESC")
    fun getAllAccessories(): Flow<List<Accessory>>

    @Query("SELECT * FROM accessories WHERE id = :id")
    suspend fun getAccessoryById(id: Long): Accessory?

    @Query("""
        SELECT * FROM accessories 
        WHERE name LIKE '%' || :query || '%' 
        OR category LIKE '%' || :query || '%' 
        ORDER BY id DESC
    """)
    fun searchAccessories(query: String): Flow<List<Accessory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccessory(accessory: Accessory): Long

    @Update
    suspend fun updateAccessory(accessory: Accessory)

    @Query("DELETE FROM accessories WHERE id = :id")
    suspend fun deleteAccessory(id: Long)

    @Query("UPDATE accessories SET quantity = :quantity WHERE id = :id")
    suspend fun updateAccessoryQuantity(id: Long, quantity: Int)

    // --- Repair Jobs ---
    @Query("SELECT * FROM repair_jobs ORDER BY id DESC")
    fun getAllRepairJobs(): Flow<List<RepairJob>>

    @Query("SELECT * FROM repair_jobs WHERE status = :status ORDER BY id DESC")
    fun getRepairJobsByStatus(status: String): Flow<List<RepairJob>>

    @Query("SELECT * FROM repair_jobs WHERE date = :date ORDER BY id DESC")
    fun getRepairJobsByDate(date: String): Flow<List<RepairJob>>

    @Query("""
        SELECT * FROM repair_jobs 
        WHERE customerName LIKE '%' || :query || '%' 
        OR customerPhone LIKE '%' || :query || '%' 
        OR brand LIKE '%' || :query || '%' 
        OR model LIKE '%' || :query || '%' 
        OR imei LIKE '%' || :query || '%'
        OR status LIKE '%' || :query || '%'
        ORDER BY id DESC
    """)
    fun searchRepairJobs(query: String): Flow<List<RepairJob>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepairJob(job: RepairJob): Long

    @Update
    suspend fun updateRepairJob(job: RepairJob)

    @Query("DELETE FROM repair_jobs WHERE id = :id")
    suspend fun deleteRepairJob(id: Long)

    // --- Customers ---
    @Query("SELECT * FROM customers ORDER BY name ASC")
    fun getAllCustomers(): Flow<List<Customer>>

    @Query("SELECT * FROM customers WHERE id = :id")
    suspend fun getCustomerById(id: Long): Customer?

    @Query("""
        SELECT * FROM customers 
        WHERE name LIKE '%' || :query || '%' 
        OR phone LIKE '%' || :query || '%' 
        ORDER BY name ASC
    """)
    fun searchCustomers(query: String): Flow<List<Customer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: Customer): Long

    @Update
    suspend fun updateCustomer(customer: Customer)

    @Query("DELETE FROM customers WHERE id = :id")
    suspend fun deleteCustomer(id: Long)

    // --- Sales / Invoices ---
    @Query("SELECT * FROM sale_invoices ORDER BY id DESC")
    fun getAllInvoices(): Flow<List<SaleInvoice>>

    @Query("SELECT * FROM sale_invoices WHERE date = :date ORDER BY id DESC")
    fun getInvoicesByDate(date: String): Flow<List<SaleInvoice>>

    @Query("SELECT * FROM sale_items WHERE invoiceId = :invoiceId")
    fun getInvoiceItems(invoiceId: Long): Flow<List<SaleItem>>

    @Query("SELECT * FROM sale_items")
    fun getAllSaleItems(): Flow<List<SaleItem>>

    @Query("""
        SELECT * FROM sale_invoices 
        WHERE customerName LIKE '%' || :query || '%' 
        OR invoiceNumber LIKE '%' || :query || '%'
        OR customerPhone LIKE '%' || :query || '%'
        ORDER BY id DESC
    """)
    fun searchInvoices(query: String): Flow<List<SaleInvoice>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: SaleInvoice): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSaleItem(item: SaleItem): Long

    @Query("DELETE FROM sale_invoices WHERE id = :id")
    suspend fun deleteInvoice(id: Long)

    @Query("DELETE FROM sale_items WHERE invoiceId = :invoiceId")
    suspend fun deleteSaleItemsForInvoice(invoiceId: Long)

    // --- Expenses ---
    @Query("SELECT * FROM expenses ORDER BY id DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE date = :date ORDER BY id DESC")
    fun getExpensesByDate(date: String): Flow<List<Expense>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense): Long

    @Update
    suspend fun updateExpense(expense: Expense)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteExpense(id: Long)

    // --- Sync & Backup / Delete All ---
    @Query("DELETE FROM mobile_stock")
    suspend fun deleteAllMobiles()

    @Query("DELETE FROM accessories")
    suspend fun deleteAllAccessories()

    @Query("DELETE FROM repair_jobs")
    suspend fun deleteAllRepairJobs()

    @Query("DELETE FROM customers")
    suspend fun deleteAllCustomers()

    @Query("DELETE FROM sale_invoices")
    suspend fun deleteAllInvoices()

    @Query("DELETE FROM sale_items")
    suspend fun deleteAllSaleItems()

    @Query("DELETE FROM expenses")
    suspend fun deleteAllExpenses()
}
