package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Category
import com.example.data.model.Product
import com.example.ui.components.ProductCard
import com.example.ui.theme.CoralAccent
import com.example.ui.theme.RoyalBlueDark
import com.example.ui.theme.RoyalBluePrimary
import com.example.ui.viewmodel.SortOption

@Composable
fun HomeScreen(
    products: List<Product>,
    categories: List<Category>,
    selectedCategory: String?,
    sortOption: SortOption,
    onCategorySelect: (String?) -> Unit,
    onSortOptionSelect: (SortOption) -> Unit,
    onProductClick: (String) -> Unit,
    onAddToCart: (String) -> Unit,
    onBuyNow: (String) -> Unit
) {
    var showSortMenu by remember { mutableStateOf(false) }

    val featuredProducts = remember(products) {
        products.filter { it.isFeatured }
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("home_products_grid")
    ) {
        // Hero Promo Banner
        item(span = { GridItemSpan(maxLineSpan) }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .testTag("hero_promo_banner"),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(RoyalBluePrimary, CoralAccent)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalOffer,
                                contentDescription = "Promo Offer",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "MEGA RESELLER DEAL",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Up to 40% OFF Top Electronics & Apparel",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Fast nationwide delivery • Genuine warranty",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Category Filter Row
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(
                    text = "Categories",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.testTag("categories_row")
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { onCategorySelect(null) },
                            label = { Text("All Products") },
                            shape = RoundedCornerShape(20.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RoyalBluePrimary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("category_chip_all")
                        )
                    }

                    items(categories) { cat ->
                        val isSelected = selectedCategory.equals(cat.name, ignoreCase = true)
                        FilterChip(
                            selected = isSelected,
                            onClick = { onCategorySelect(cat.name) },
                            label = { Text(cat.name) },
                            shape = RoundedCornerShape(20.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RoyalBluePrimary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("category_chip_${cat.id}")
                        )
                    }
                }
            }
        }

        // Featured Products Section (if any)
        if (featuredProducts.isNotEmpty() && selectedCategory == null) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Featured",
                                tint = CoralAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Featured Products",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.testTag("featured_products_row")
                    ) {
                        items(featuredProducts) { product ->
                            ProductCard(
                                product = product,
                                onProductClick = { onProductClick(product.id) },
                                onAddToCart = { onAddToCart(product.id) },
                                onBuyNow = { onBuyNow(product.id) },
                                modifier = Modifier.width(180.dp)
                            )
                        }
                    }
                }
            }
        }

        // Header Section with Sort Controls
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (selectedCategory != null) "$selectedCategory (${products.size})" else "All Products (${products.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Box {
                    AssistChip(
                        onClick = { showSortMenu = true },
                        label = { Text(sortOption.label, fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Sort,
                                contentDescription = "Sort",
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        modifier = Modifier.testTag("sort_chip_button")
                    )

                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        SortOption.entries.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.label) },
                                onClick = {
                                    onSortOptionSelect(option)
                                    showSortMenu = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Products Grid
        if (products.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No products found",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try clearing filters or search query",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(products) { product ->
                ProductCard(
                    product = product,
                    onProductClick = { onProductClick(product.id) },
                    onAddToCart = { onAddToCart(product.id) },
                    onBuyNow = { onBuyNow(product.id) }
                )
            }
        }
    }
}
