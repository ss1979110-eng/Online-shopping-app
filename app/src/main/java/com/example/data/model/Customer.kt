package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey
    val id: String,
    val name: String,
    val phone: String,
    val email: String,
    val city: String,
    val isBlocked: Boolean = false,
    val totalOrdersCount: Int = 0,
    val registeredDate: Long = System.currentTimeMillis()
)
