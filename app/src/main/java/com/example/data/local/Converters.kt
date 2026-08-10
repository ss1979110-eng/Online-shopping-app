package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.OrderStatus

class Converters {
    @TypeConverter
    fun fromOrderStatus(status: OrderStatus): String {
        return status.name
    }

    @TypeConverter
    fun toOrderStatus(value: String): OrderStatus {
        return OrderStatus.fromString(value)
    }
}
