package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AdminPrimary
import com.example.ui.theme.CoralAccent
import com.example.ui.theme.RoyalBlueDark
import com.example.ui.theme.RoyalBluePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarHeader(
    appName: String = "ShopSphere",
    isAdminMode: Boolean,
    cartCount: Int,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onCartClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onAdminToggle: (Boolean) -> Unit,
    onHomeClick: () -> Unit
) {
    Surface(
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Main Top Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (isAdminMode) {
                            Brush.horizontalGradient(
                                colors = listOf(AdminPrimary, Color(0xFF312E81))
                            )
                        } else {
                            Brush.horizontalGradient(
                                colors = listOf(RoyalBluePrimary, RoyalBlueDark)
                            )
                        }
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Logo and Title
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { onHomeClick() }
                            .testTag("app_logo_button")
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (isAdminMode) Icons.Default.AdminPanelSettings else Icons.Default.ShoppingBag,
                                contentDescription = "App Icon",
                                tint = Color.White,
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = appName,
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isAdminMode) "Admin Management" else "Store & Reseller",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Action Items
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (!isAdminMode) {
                            // My Orders Button
                            IconButton(
                                onClick = onOrdersClick,
                                modifier = Modifier.testTag("orders_nav_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                                    contentDescription = "My Orders",
                                    tint = Color.White
                                )
                            }

                            // Cart Badge Icon
                            BadgedBox(
                                badge = {
                                    if (cartCount > 0) {
                                        Badge(
                                            containerColor = CoralAccent,
                                            contentColor = Color.White
                                        ) {
                                            Text(text = cartCount.toString())
                                        }
                                    }
                                },
                                modifier = Modifier.testTag("cart_nav_button")
                            ) {
                                IconButton(onClick = onCartClick) {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingCart,
                                        contentDescription = "Shopping Cart",
                                        tint = Color.White
                                    )
                                }
                            }
                        }

                        // Mode Toggle Chip
                        FilterChip(
                            selected = isAdminMode,
                            onClick = { onAdminToggle(!isAdminMode) },
                            label = {
                                Text(
                                    text = if (isAdminMode) "Admin" else "Customer",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (isAdminMode) Icons.Default.Shield else Icons.Default.Person,
                                    contentDescription = "Toggle Mode",
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = Color.White.copy(alpha = 0.2f),
                                labelColor = Color.White,
                                iconColor = Color.White,
                                selectedContainerColor = Color.White,
                                selectedLabelColor = AdminPrimary,
                                selectedLeadingIconColor = AdminPrimary
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("mode_toggle_chip")
                        )
                    }
                }
            }

            // Search Bar (Customer Mode)
            AnimatedVisibility(visible = !isAdminMode) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_bar_input"),
                        placeholder = { Text("Search products, categories...", fontSize = 14.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchQueryChange("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear search"
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }
        }
    }
}
