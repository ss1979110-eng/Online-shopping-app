package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AdminPrimary
import com.example.ui.theme.CoralAccent
import com.example.ui.theme.MintSuccess
import com.example.ui.theme.RoyalBluePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    totalProducts: Int,
    totalCustomers: Int,
    totalOrders: Int,
    pendingOrders: Int,
    completedOrders: Int,
    totalSales: Double,
    onNavigateToProducts: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToCustomers: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Business Overview",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Metrics Grid (2x3)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "Total Sales",
                value = "$${String.format("%.2f", totalSales)}",
                icon = Icons.Default.AttachMoney,
                color = MintSuccess,
                modifier = Modifier
                    .weight(1f)
                    .testTag("metric_total_sales")
            )

            MetricCard(
                title = "Total Orders",
                value = "$totalOrders",
                icon = Icons.AutoMirrored.Filled.ReceiptLong,
                color = RoyalBluePrimary,
                modifier = Modifier
                    .weight(1f)
                    .testTag("metric_total_orders")
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "Pending Orders",
                value = "$pendingOrders",
                icon = Icons.Default.PendingActions,
                color = CoralAccent,
                modifier = Modifier
                    .weight(1f)
                    .testTag("metric_pending_orders")
            )

            MetricCard(
                title = "Completed",
                value = "$completedOrders",
                icon = Icons.Default.CheckCircle,
                color = MintSuccess,
                modifier = Modifier
                    .weight(1f)
                    .testTag("metric_completed_orders")
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "Total Products",
                value = "$totalProducts",
                icon = Icons.Default.Inventory,
                color = AdminPrimary,
                modifier = Modifier
                    .weight(1f)
                    .testTag("metric_total_products")
            )

            MetricCard(
                title = "Customers",
                value = "$totalCustomers",
                icon = Icons.Default.People,
                color = Color(0xFF8B5CF6),
                modifier = Modifier
                    .weight(1f)
                    .testTag("metric_total_customers")
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Management Control",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Navigation Management Cards
        ManagementNavCard(
            title = "Product Management",
            description = "Add, edit, delete & manage product catalog",
            icon = Icons.Default.ShoppingBag,
            color = RoyalBluePrimary,
            onClick = onNavigateToProducts,
            modifier = Modifier.testTag("admin_nav_products")
        )

        ManagementNavCard(
            title = "Order Management",
            description = "Track orders, update statuses & details",
            icon = Icons.AutoMirrored.Filled.ReceiptLong,
            color = CoralAccent,
            onClick = onNavigateToOrders,
            modifier = Modifier.testTag("admin_nav_orders")
        )

        ManagementNavCard(
            title = "Customer Management",
            description = "View registered customers & block/unblock accounts",
            icon = Icons.Default.People,
            color = AdminPrimary,
            onClick = onNavigateToCustomers,
            modifier = Modifier.testTag("admin_nav_customers")
        )
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color.copy(alpha = 0.15f), shape = RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ManagementNavCard(
    title: String,
    description: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(color.copy(alpha = 0.15f), shape = RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(text = description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
