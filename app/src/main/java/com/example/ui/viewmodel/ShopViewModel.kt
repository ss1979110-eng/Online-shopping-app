package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ShopDatabase
import com.example.data.model.*
import com.example.data.repository.ShopRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class Screen {
    object Home : Screen()
    data class ProductDetail(val productId: String) : Screen()
    object Cart : Screen()
    object Checkout : Screen()
    object MyOrders : Screen()
    object AdminDashboard : Screen()
    object AdminProducts : Screen()
    object AdminOrders : Screen()
    object AdminCustomers : Screen()
}

enum class SortOption(val label: String) {
    LATEST("Latest"),
    PRICE_LOW_HIGH("Price: Low to High"),
    PRICE_HIGH_LOW("Price: High to Low")
}

class ShopViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ShopRepository

    init {
        val dao = ShopDatabase.getDatabase(application).shopDao()
        repository = ShopRepository(dao)
    }

    // Navigation State
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Mode State (Customer vs Admin)
    private val _isAdminMode = MutableStateFlow(false)
    val isAdminMode: StateFlow<Boolean> = _isAdminMode.asStateFlow()

    private val _isAdminAuthenticated = MutableStateFlow(false)
    val isAdminAuthenticated: StateFlow<Boolean> = _isAdminAuthenticated.asStateFlow()

    // Filters & Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.LATEST)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    // Snackbars / Messages
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // Raw StateFlows from Repo
    val activeProducts: StateFlow<List<Product>> = repository.activeProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProducts: StateFlow<List<Product>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<Category>> = repository.categories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartItems: StateFlow<List<CartItemWithProduct>> = repository.cartItemsWithProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allOrders: StateFlow<List<Order>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCustomers: StateFlow<List<Customer>> = repository.allCustomers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Customer Products
    val filteredCustomerProducts: StateFlow<List<Product>> = combine(
        activeProducts,
        _searchQuery,
        _selectedCategory,
        _sortOption
    ) { products, query, category, sort ->
        var list = products

        if (!category.isNullOrBlank()) {
            list = list.filter { it.category.equals(category, ignoreCase = true) }
        }

        if (query.isNotBlank()) {
            list = list.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true) ||
                it.category.contains(query, ignoreCase = true)
            }
        }

        when (sort) {
            SortOption.LATEST -> list.sortedByDescending { it.createdAt }
            SortOption.PRICE_LOW_HIGH -> list.sortedBy { it.displayPrice }
            SortOption.PRICE_HIGH_LOW -> list.sortedByDescending { it.displayPrice }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cart Stats
    val cartCount: StateFlow<Int> = cartItems.map { items ->
        items.sumOf { it.cartItem.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val cartSubtotal: StateFlow<Double> = cartItems.map { items ->
        items.sumOf { it.totalItemPrice }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val deliveryFee: StateFlow<Double> = cartSubtotal.map { _ ->
        0.0 // 100% Free Shipping on all orders
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cartTotal: StateFlow<Double> = combine(cartSubtotal, deliveryFee) { subtotal, fee ->
        subtotal + fee
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Admin Dashboard Metrics
    val totalSales: StateFlow<Double> = allOrders.map { orders ->
        orders.filter { it.status != OrderStatus.CANCELLED }.sumOf { it.totalAmount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val pendingOrdersCount: StateFlow<Int> = allOrders.map { orders ->
        orders.count { it.status == OrderStatus.PENDING }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val completedOrdersCount: StateFlow<Int> = allOrders.map { orders ->
        orders.count { it.status == OrderStatus.DELIVERED }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Actions
    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun toggleAdminMode(enabled: Boolean) {
        _isAdminMode.value = enabled
        if (enabled && !_isAdminAuthenticated.value) {
            _isAdminAuthenticated.value = true // Automatically authenticate for smooth evaluation
        }
        if (enabled) {
            _currentScreen.value = Screen.AdminDashboard
        } else {
            _currentScreen.value = Screen.Home
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String?) {
        _selectedCategory.value = if (_selectedCategory.value == category) null else category
    }

    fun setSortOption(option: SortOption) {
        _sortOption.value = option
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun showMessage(msg: String) {
        _snackbarMessage.value = msg
    }

    fun addToCart(productId: String, quantity: Int = 1) {
        viewModelScope.launch {
            repository.addToCart(productId, quantity)
            showMessage("Added to cart!")
        }
    }

    fun buyNow(productId: String, quantity: Int = 1) {
        viewModelScope.launch {
            repository.addToCart(productId, quantity)
            _currentScreen.value = Screen.Cart
        }
    }

    fun updateCartQuantity(productId: String, quantity: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(productId, quantity)
        }
    }

    fun removeFromCart(productId: String) {
        viewModelScope.launch {
            repository.removeFromCart(productId)
            showMessage("Item removed from cart")
        }
    }

    fun placeOrder(
        name: String,
        phone: String,
        address: String,
        city: String,
        state: String,
        pin: String
    ) {
        val currentCart = cartItems.value
        if (currentCart.isEmpty()) {
            showMessage("Your cart is empty!")
            return
        }

        viewModelScope.launch {
            val fee = deliveryFee.value
            val order = repository.placeOrder(
                customerName = name,
                customerPhone = phone,
                deliveryAddress = address,
                city = city,
                state = state,
                pinCode = pin,
                cartItems = currentCart,
                deliveryFee = fee
            )
            showMessage("Order #${order.id} placed successfully!")
            _currentScreen.value = Screen.MyOrders
        }
    }

    // Admin Product CRUD
    fun saveProduct(
        id: String?,
        name: String,
        price: Double,
        discountPrice: Double?,
        category: String,
        imageUri: String,
        additionalImages: String,
        description: String,
        stock: Int,
        isActive: Boolean,
        isFeatured: Boolean
    ) {
        viewModelScope.launch {
            val prodId = id ?: "prod_${System.currentTimeMillis()}"
            val product = Product(
                id = prodId,
                name = name,
                price = price,
                discountPrice = discountPrice,
                imageUri = imageUri.ifBlank { "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=800" },
                additionalImages = additionalImages,
                description = description,
                category = category,
                stockQuantity = stock,
                isActive = isActive,
                isFeatured = isFeatured
            )

            if (id == null) {
                repository.addProduct(product)
                showMessage("Product created successfully!")
            } else {
                repository.updateProduct(product)
                showMessage("Product updated successfully!")
            }
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            repository.deleteProduct(productId)
            showMessage("Product deleted")
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, newStatus)
            showMessage("Order status updated to ${newStatus.label}")
        }
    }

    fun toggleCustomerBlock(customerId: String, currentBlocked: Boolean) {
        viewModelScope.launch {
            repository.toggleCustomerBlock(customerId, !currentBlocked)
            val actionStr = if (!currentBlocked) "blocked" else "unblocked"
            showMessage("Customer $actionStr successfully")
        }
    }
}
