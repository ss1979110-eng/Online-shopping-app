package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey
    val productId: String,
    val quantity: Int
)

data class CartItemWithProduct(
    val cartItem: CartItem,
    val product: Product
) {
    val totalItemPrice: Double
        get() = product.displayPrice * cartItem.quantity
}
