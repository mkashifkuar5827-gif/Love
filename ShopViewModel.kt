package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.ShopDatabase
import com.example.data.entity.*
import com.example.data.repository.ShopRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class CartItem(
    val itemType: String, // "Mobile" or "Accessory"
    val itemId: Long,
    val name: String,
    val unitPrice: Double,
    var quantity: Int,
    val maxAvailable: Int
) {
    val subtotal: Double
        get() = unitPrice * quantity
}

class ShopViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ShopRepository

    init {
        val database = ShopDatabase.getDatabase(application)
        repository = ShopRepository(database.shopDao())
    }

    // Flows from DB
    val mobiles: StateFlow<List<MobileStock>> = repository.allMobiles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val accessories: StateFlow<List<Accessory>> = repository.allAccessories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val repairJobs: StateFlow<List<RepairJob>> = repository.allRepairJobs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customers: StateFlow<List<Customer>> = repository.allCustomers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val invoices: StateFlow<List<SaleInvoice>> = repository.allInvoices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val saleItems: StateFlow<List<SaleItem>> = repository.allSaleItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenses: StateFlow<List<Expense>> = repository.allExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Date for Daily Repairs & Daily Reports
    private val _selectedDate = MutableStateFlow(getTodayDateString())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
    }

    // Universal Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // POS Cart State
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    fun addToCart(cartItem: CartItem) {
        val current = _cartItems.value.toMutableList()
        val existingIndex = current.indexOfFirst { it.itemType == cartItem.itemType && it.itemId == cartItem.itemId }
        if (existingIndex >= 0) {
            val existing = current[existingIndex]
            val newQty = (existing.quantity + cartItem.quantity).coerceAtMost(existing.maxAvailable)
            current[existingIndex] = existing.copy(quantity = newQty)
        } else {
            current.add(cartItem)
        }
        _cartItems.value = current
    }

    fun updateCartItemQuantity(index: Int, newQuantity: Int) {
        val current = _cartItems.value.toMutableList()
        if (index in current.indices) {
            if (newQuantity <= 0) {
                current.removeAt(index)
            } else {
                val item = current[index]
                current[index] = item.copy(quantity = newQuantity.coerceAtMost(item.maxAvailable))
            }
            _cartItems.value = current
        }
    }

    fun removeFromCart(index: Int) {
        val current = _cartItems.value.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            _cartItems.value = current
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    // Last generated invoice for showing receipt
    private val _lastGeneratedInvoice = MutableStateFlow<SaleInvoice?>(null)
    val lastGeneratedInvoice: StateFlow<SaleInvoice?> = _lastGeneratedInvoice.asStateFlow()

    private val _lastGeneratedItems = MutableStateFlow<List<SaleItem>>(emptyList())
    val lastGeneratedItems: StateFlow<List<SaleItem>> = _lastGeneratedItems.asStateFlow()

    fun clearLastInvoice() {
        _lastGeneratedInvoice.value = null
        _lastGeneratedItems.value = emptyList()
    }

    // --- Mobile Stock Actions ---
    fun addMobile(mobile: MobileStock) = viewModelScope.launch {
        repository.insertMobile(mobile)
    }

    fun updateMobile(mobile: MobileStock) = viewModelScope.launch {
        repository.updateMobile(mobile)
    }

    fun deleteMobile(id: Long) = viewModelScope.launch {
        repository.deleteMobile(id)
    }

    fun updateMobileQty(mobile: MobileStock, delta: Int) = viewModelScope.launch {
        val newQty = (mobile.quantity + delta).coerceAtLeast(0)
        repository.updateMobileQuantity(mobile.id, newQty)
    }

    // --- Accessory Actions ---
    fun addAccessory(accessory: Accessory) = viewModelScope.launch {
        repository.insertAccessory(accessory)
    }

    fun updateAccessory(accessory: Accessory) = viewModelScope.launch {
        repository.updateAccessory(accessory)
    }

    fun deleteAccessory(id: Long) = viewModelScope.launch {
        repository.deleteAccessory(id)
    }

    fun updateAccessoryQty(accessory: Accessory, delta: Int) = viewModelScope.launch {
        val newQty = (accessory.quantity + delta).coerceAtLeast(0)
        repository.updateAccessoryQuantity(accessory.id, newQty)
    }

    // --- Repair Job Actions ---
    fun addRepairJob(job: RepairJob) = viewModelScope.launch {
        repository.insertRepairJob(job)
    }

    fun updateRepairJob(job: RepairJob) = viewModelScope.launch {
        repository.updateRepairJob(job)
    }

    fun deleteRepairJob(id: Long) = viewModelScope.launch {
        repository.deleteRepairJob(id)
    }

    // --- Customer Actions ---
    fun addCustomer(customer: Customer) = viewModelScope.launch {
        repository.insertCustomer(customer)
    }

    fun updateCustomer(customer: Customer) = viewModelScope.launch {
        repository.updateCustomer(customer)
    }

    fun deleteCustomer(id: Long) = viewModelScope.launch {
        repository.deleteCustomer(id)
    }

    // --- Expense Actions ---
    fun addExpense(expense: Expense) = viewModelScope.launch {
        repository.insertExpense(expense)
    }

    fun updateExpense(expense: Expense) = viewModelScope.launch {
        repository.updateExpense(expense)
    }

    fun deleteExpense(id: Long) = viewModelScope.launch {
        repository.deleteExpense(id)
    }

    // --- Sale / POS Action ---
    fun completeSale(
        customerName: String,
        customerPhone: String,
        discount: Double,
        paidAmount: Double,
        notes: String = "",
        onComplete: (SaleInvoice) -> Unit
    ) = viewModelScope.launch {
        val cart = _cartItems.value
        if (cart.isEmpty()) return@launch

        val rawTotal = cart.sumOf { it.subtotal }
        val finalTotal = (rawTotal - discount).coerceAtLeast(0.0)
        val remaining = (finalTotal - paidAmount).coerceAtLeast(0.0)
        val invoiceNum = "INV-" + (System.currentTimeMillis() % 1000000)

        val invoice = SaleInvoice(
            invoiceNumber = invoiceNum,
            customerName = if (customerName.isBlank()) "Walk-in Customer" else customerName,
            customerPhone = customerPhone,
            date = getTodayDateString(),
            totalAmount = rawTotal,
            discount = discount,
            finalTotal = finalTotal,
            paidAmount = paidAmount,
            remainingBalance = remaining,
            notes = notes
        )

        val saleItemsList = cart.map {
            SaleItem(
                invoiceId = 0,
                itemType = it.itemType,
                itemId = it.itemId,
                description = it.name,
                unitPrice = it.unitPrice,
                quantity = it.quantity,
                subtotal = it.subtotal
            )
        }

        val invoiceId = repository.processSale(invoice, saleItemsList)
        val createdInvoice = invoice.copy(id = invoiceId)

        _lastGeneratedInvoice.value = createdInvoice
        _lastGeneratedItems.value = saleItemsList.map { it.copy(invoiceId = invoiceId) }
        clearCart()

        onComplete(createdInvoice)
    }

    fun deleteInvoice(id: Long) = viewModelScope.launch {
        repository.deleteInvoice(id)
    }

    // --- Backup & Restore ---
    private val _backupStatus = MutableStateFlow<String?>(null)
    val backupStatus: StateFlow<String?> = _backupStatus.asStateFlow()

    fun clearBackupStatus() {
        _backupStatus.value = null
    }

    fun exportDatabaseJson(onSuccess: (String) -> Unit) = viewModelScope.launch {
        val json = repository.exportBackupJson(getTodayDateString())
        onSuccess(json)
    }

    fun importDatabaseJson(jsonString: String) = viewModelScope.launch {
        val success = repository.importBackupJson(jsonString)
        _backupStatus.value = if (success) "Database restored successfully!" else "Failed to restore database. Invalid format."
    }

    companion object {
        fun getTodayDateString(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return sdf.format(Date())
        }
    }
}
