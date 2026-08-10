package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Product::class, Category::class, CartItem::class, Order::class, Customer::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ShopDatabase : RoomDatabase() {

    abstract fun shopDao(): ShopDao

    companion object {
        @Volatile
        private var INSTANCE: ShopDatabase? = null

        fun getDatabase(context: Context): ShopDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ShopDatabase::class.java,
                    "shopsphere_database"
                )
                .addCallback(DatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database.shopDao())
                    }
                }
            }

            suspend fun populateInitialData(dao: ShopDao) {
                // Initial Categories
                val categories = listOf(
                    Category("cat_1", "Electronics", "laptop", "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500"),
                    Category("cat_2", "Fashion", "apparel", "https://images.unsplash.com/photo-1489987707025-afc232f7ea0f?w=500"),
                    Category("cat_3", "Home & Living", "home", "https://images.unsplash.com/photo-1513694203232-719a280e022f?w=500"),
                    Category("cat_4", "Footwear", "shoe", "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=500"),
                    Category("cat_5", "Accessories", "watch", "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500")
                )
                dao.insertCategories(categories)

                // Initial Sample Products
                val products = listOf(
                    Product(
                        id = "prod_101",
                        name = "ProSound Noise-Canceling Wireless Headphones",
                        price = 149.99,
                        discountPrice = 119.99,
                        imageUri = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800",
                        additionalImages = "https://images.unsplash.com/photo-1484704849700-f032a568e944?w=800,https://images.unsplash.com/photo-1546435770-a3e426bf472b?w=800",
                        description = "Experience premium active noise cancellation with 40-hour battery life, plush memory foam earcups, and custom EQ audio tuning.",
                        category = "Electronics",
                        stockQuantity = 24,
                        isActive = true,
                        isFeatured = true,
                        rating = 4.8f
                    ),
                    Product(
                        id = "prod_102",
                        name = "Apex Smart Fitness Watch v2",
                        price = 199.00,
                        discountPrice = 159.00,
                        imageUri = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=800",
                        additionalImages = "https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?w=800",
                        description = "Track HR, SpO2, sleep quality, and 50+ workout modes. Full HD AMOLED display with 7-day battery life.",
                        category = "Accessories",
                        stockQuantity = 18,
                        isActive = true,
                        isFeatured = true,
                        rating = 4.7f
                    ),
                    Product(
                        id = "prod_103",
                        name = "Urban Runner Pro Sneakers",
                        price = 120.00,
                        discountPrice = 89.99,
                        imageUri = "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=800",
                        additionalImages = "https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=800",
                        description = "Lightweight breathable mesh knit upper with cloud-cushion responsive foam sole. Ultra comfortable for daily wear.",
                        category = "Footwear",
                        stockQuantity = 30,
                        isActive = true,
                        isFeatured = true,
                        rating = 4.9f
                    ),
                    Product(
                        id = "prod_104",
                        name = "Minimalist Waterproof Travel Backpack 25L",
                        price = 79.99,
                        discountPrice = 59.99,
                        imageUri = "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=800",
                        additionalImages = "https://images.unsplash.com/photo-1622560480605-d83c853bc5c3?w=800",
                        description = "Sleek 15.6\" padded laptop compartment, anti-theft hidden pocket, USB charging port, and water-repellent fabric.",
                        category = "Fashion",
                        stockQuantity = 12,
                        isActive = true,
                        isFeatured = false,
                        rating = 4.6f
                    ),
                    Product(
                        id = "prod_105",
                        name = "Smart Espresso & Drip Coffee Machine",
                        price = 249.99,
                        discountPrice = 199.99,
                        imageUri = "https://images.unsplash.com/photo-1517256064527-09c73fc73e38?w=800",
                        additionalImages = "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=800",
                        description = "19-bar extraction pump with built-in milk frother and programmable timer for fresh barista quality coffee.",
                        category = "Home & Living",
                        stockQuantity = 8,
                        isActive = true,
                        isFeatured = true,
                        rating = 4.8f
                    ),
                    Product(
                        id = "prod_106",
                        name = "Retro Polarized UV400 Sunglasses",
                        price = 45.00,
                        discountPrice = 29.99,
                        imageUri = "https://images.unsplash.com/photo-1511499767150-a48a237f0083?w=800",
                        additionalImages = "https://images.unsplash.com/photo-1572635196237-14b3f281503f?w=800",
                        description = "Classic lightweight acetate frame with TAC HD polarized lenses that eliminate glare and block 100% harmful UV rays.",
                        category = "Accessories",
                        stockQuantity = 40,
                        isActive = true,
                        isFeatured = false,
                        rating = 4.5f
                    )
                )
                dao.insertProducts(products)

                // Initial Customers
                val customers = listOf(
                    Customer("cust_1", "Alex Morgan", "+1 (555) 234-5678", "alex.m@example.com", "New York", false, 3),
                    Customer("cust_2", "Sarah Jenkins", "+1 (555) 876-5432", "s.jenkins@example.com", "Chicago", false, 1),
                    Customer("cust_3", "David Miller", "+1 (555) 345-6789", "dmiller@example.com", "Austin", false, 0)
                )
                dao.insertCustomers(customers)

                // Initial Orders
                val orders = listOf(
                    Order(
                        id = "ORD-8921",
                        customerName = "Alex Morgan",
                        customerPhone = "+1 (555) 234-5678",
                        deliveryAddress = "742 Evergreen Terrace, Apt 4B",
                        city = "New York",
                        state = "NY",
                        pinCode = "10001",
                        itemsSummaryJson = "ProSound Headphones x 1, Urban Runner Sneakers x 1",
                        totalAmount = "209.98".toDouble(),
                        deliveryCharge = 0.0,
                        orderDate = System.currentTimeMillis() - 86400000L * 2,
                        status = OrderStatus.DELIVERED
                    ),
                    Order(
                        id = "ORD-9104",
                        customerName = "Sarah Jenkins",
                        customerPhone = "+1 (555) 876-5432",
                        deliveryAddress = "128 W Lake St",
                        city = "Chicago",
                        state = "IL",
                        pinCode = "60601",
                        itemsSummaryJson = "Apex Smart Watch v2 x 1",
                        totalAmount = "159.00".toDouble(),
                        deliveryCharge = 4.99,
                        orderDate = System.currentTimeMillis() - 3600000L * 5,
                        status = OrderStatus.PENDING
                    )
                )
                for (o in orders) {
                    dao.insertOrder(o)
                }
            }
        }
    }
}
