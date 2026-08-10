package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey
    val id: String,
    val name: String,
    val price: Double,
    val discountPrice: Double? = null,
    val imageUri: String, // Main image or primary URL
    val additionalImages: String = "", // Pipeline/comma separated additional image URLs
    val description: String,
    val category: String,
    val stockQuantity: Int,
    val isActive: Boolean = true,
    val isFeatured: Boolean = false,
    val rating: Float = 4.5f,
    val createdAt: Long = System.currentTimeMillis()
) {
    val displayPrice: Double
        get() = discountPrice ?: price

    val discountPercentage: Int
        get() {
            if (discountPrice == null || discountPrice >= price) return 0
            return (((price - discountPrice) / price) * 100).toInt()
        }

    val allImages: List<String>
        get() {
            val list = mutableListOf(imageUri)
            if (additionalImages.isNotBlank()) {
                list.addAll(additionalImages.split(",").map { it.trim() }.filter { it.isNotEmpty() })
            }
            return list.distinct()
        }
}
