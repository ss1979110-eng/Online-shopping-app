package com.example.data.repository

import com.example.data.local.ShopDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ShopRepository(private val dao: ShopDao) {

    val allProducts: Flow<List<Product>> = dao.getAllProducts()
    val activeProducts: Flow<List<Product>> = dao.getActiveProducts()
    val categories: Flow<List<Category>> = dao.getAllCategories()
    val allOrders: Flow<List<Order>> = dao.getAllOrders()
    val allCustomers: Flow<List<Customer>> = dao.getAllCustomers()

    val cartItemsWithProducts: Flow<List<CartItemWithProduct>> = combine(
        dao.getAllCartItems(),
        dao.getAllProducts()
    ) { cartItems, products ->
        cartItems.mapNotNull { cartItem ->
            val product = products.find { it.id == cartItem.productId }
            product?.let { CartItemWithProduct(cartItem, it) }
        }
    }

    suspend fun getProductById(id: String): Product? = dao.getProductByIdDirect(id)

    suspend fun addProduct(product: Product) {
        dao.insertProduct(product)
    }

    suspend fun updateProduct(product: Product) {
        dao.updateProduct(product)
    }

    suspend fun deleteProduct(id: String) {
        dao.deleteProductById(id)
    }

    suspend fun addToCart(productId: String, quantity: Int = 1) {
        dao.insertCartItem(CartItem(productId, quantity))
    }

    suspend fun updateCartQuantity(productId: String, quantity: Int) {
        if (quantity <= 0) {
            dao.deleteCartItem(productId)
        } else {
            dao.updateCartQuantity(productId, quantity)
        }
    }

    suspend fun removeFromCart(productId: String) {
        dao.deleteCartItem(productId)
    }

    suspend fun clearCart() {
        dao.clearCart()
    }

    suspend fun placeOrder(
        customerName: String,
        customerPhone: String,
        deliveryAddress: String,
        city: String,
        state: String,
        pinCode: String,
        cartItems: List<CartItemWithProduct>,
        deliveryFee: Double
    ): Order {
        val orderId = "ORD-${(1000..9999).random()}"
        val itemsSummary = cartItems.joinToString(", ") { "${it.product.name} x ${it.cartItem.quantity}" }
        val subtotal = cartItems.sumOf { it.totalItemPrice }
        val total = subtotal + deliveryFee

        val order = Order(
            id = orderId,
            customerName = customerName,
            customerPhone = customerPhone,
            deliveryAddress = deliveryAddress,
            city = city,
            state = state,
            pinCode = pinCode,
            itemsSummaryJson = itemsSummary,
            totalAmount = total,
            deliveryCharge = deliveryFee,
            orderDate = System.currentTimeMillis(),
            status = OrderStatus.PENDING
        )

        dao.insertOrder(order)
        dao.incrementCustomerOrderCount(customerPhone, customerName)
        dao.clearCart()
        return order
    }

    suspend fun updateOrderStatus(orderId: String, status: OrderStatus) {
        dao.updateOrderStatus(orderId, status)
    }

    suspend fun toggleCustomerBlock(customerId: String, isBlocked: Boolean) {
        dao.updateCustomerBlockStatus(customerId, isBlocked)
    }

    suspend fun addCategory(category: Category) {
        dao.insertCategory(category)
    }
}
