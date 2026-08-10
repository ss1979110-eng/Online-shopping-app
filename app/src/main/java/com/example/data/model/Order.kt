package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class OrderStatus(val label: String) {
    PENDING("Pending"),
    CONFIRMED("Confirmed"),
    SHIPPED("Shipped"),
    OUT_FOR_DELIVERY("Out for Delivery"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled");

    companion object {
        fun fromString(value: String): OrderStatus {
            return entries.find { it.name.equals(value, ignoreCase = true) || it.label.equals(value, ignoreCase = true) }
                ?: PENDING
        }
    }
}

data class OrderItemSummary(
    val productId: String,
    val productName: String,
    val price: Double,
    val quantity: Int,
    val imageUri: String
)

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey
    val id: String, // e.g. ORD-9482
    val customerName: String,
    val customerPhone: String,
    val deliveryAddress: String,
    val city: String,
    val state: String,
    val pinCode: String,
    val itemsSummaryJson: String, // Stored as comma or pipeline formatted or concise string
    val totalAmount: Double,
    val deliveryCharge: Double,
    val orderDate: Long = System.currentTimeMillis(),
    val status: OrderStatus = OrderStatus.PENDING
)
