package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Category
import com.example.data.model.Product
import com.example.ui.theme.AdminPrimary
import com.example.ui.theme.CoralAccent
import com.example.ui.theme.MintSuccess
import com.example.ui.theme.RoyalBluePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductManageScreen(
    products: List<Product>,
    categories: List<Category>,
    onBackClick: () -> Unit,
    onSaveProduct: (String?, String, Double, Double?, String, String, String, String, Int, Boolean, Boolean) -> Unit,
    onDeleteProduct: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCatFilter by remember { mutableStateOf<String?>(null) }
    var showAddEditDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<Product?>(null) }

    val filteredProducts = remember(products, searchQuery, selectedCatFilter) {
        products.filter { prod ->
            val matchesQuery = searchQuery.isBlank() || prod.name.contains(searchQuery, ignoreCase = true) || prod.category.contains(searchQuery, ignoreCase = true)
            val matchesCat = selectedCatFilter == null || prod.category.equals(selectedCatFilter, ignoreCase = true)
            matchesQuery && matchesCat
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Management", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.testTag("admin_products_back")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    editingProduct = null
                    showAddEditDialog = true
                },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add Product") },
                containerColor = AdminPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_product_fab")
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Search & Category Filters
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search products...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_product_search"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedCatFilter == null,
                        onClick = { selectedCatFilter = null },
                        label = { Text("All") }
                    )
                }
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCatFilter == cat.name,
                        onClick = { selectedCatFilter = if (selectedCatFilter == cat.name) null else cat.name },
                        label = { Text(cat.name) }
                    )
                }
            }

            // Products List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(filteredProducts) { product ->
                    AdminProductCard(
                        product = product,
                        onEdit = {
                            editingProduct = product
                            showAddEditDialog = true
                        },
                        onDelete = { onDeleteProduct(product.id) },
                        onToggleActive = { isActive ->
                            onSaveProduct(
                                product.id,
                                product.name,
                                product.price,
                                product.discountPrice,
                                product.category,
                                product.imageUri,
                                product.additionalImages,
                                product.description,
                                product.stockQuantity,
                                isActive,
                                product.isFeatured
                            )
                        }
                    )
                }
            }
        }
    }

    if (showAddEditDialog) {
        AddEditProductDialog(
            product = editingProduct,
            categories = categories,
            onDismiss = { showAddEditDialog = false },
            onSave = { name, price, discountPrice, category, imageUri, additionalImages, description, stock, isActive, isFeatured ->
                onSaveProduct(
                    editingProduct?.id,
                    name,
                    price,
                    discountPrice,
                    category,
                    imageUri,
                    additionalImages,
                    description,
                    stock,
                    isActive,
                    isFeatured
                )
                showAddEditDialog = false
            }
        )
    }
}

@Composable
fun AdminProductCard(
    product: Product,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleActive: (Boolean) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_product_card_${product.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF1F5F9))
            ) {
                AsyncImage(
                    model = product.imageUri,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${product.category} • Stock: ${product.stockQuantity}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$${String.format("%.2f", product.displayPrice)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = RoyalBluePrimary
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (product.isActive) "Active" else "Inactive",
                        fontSize = 10.sp,
                        color = if (product.isActive) MintSuccess else MaterialTheme.colorScheme.error
                    )
                    Switch(
                        checked = product.isActive,
                        onCheckedChange = onToggleActive,
                        modifier = Modifier
                            .scale(0.7f)
                            .testTag("toggle_active_${product.id}")
                    )
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.testTag("edit_product_${product.id}")) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = AdminPrimary)
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.testTag("delete_product_${product.id}")) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
fun AddEditProductDialog(
    product: Product?,
    categories: List<Category>,
    onDismiss: () -> Unit,
    onSave: (String, Double, Double?, String, String, String, String, Int, Boolean, Boolean) -> Unit
) {
    var name by remember { mutableStateOf(product?.name ?: "") }
    var priceStr by remember { mutableStateOf(product?.price?.toString() ?: "") }
    var discountPriceStr by remember { mutableStateOf(product?.discountPrice?.toString() ?: "") }
    var category by remember { mutableStateOf(product?.category ?: if (categories.isNotEmpty()) categories[0].name else "General") }
    var imageUri by remember { mutableStateOf(product?.imageUri ?: "") }
    var additionalImages by remember { mutableStateOf(product?.additionalImages ?: "") }
    var description by remember { mutableStateOf(product?.description ?: "") }
    var stockStr by remember { mutableStateOf(product?.stockQuantity?.toString() ?: "10") }
    var isActive by remember { mutableStateOf(product?.isActive ?: true) }
    var isFeatured by remember { mutableStateOf(product?.isFeatured ?: false) }

    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (product == null) "Add New Product" else "Edit Product", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Product Name") },
                    modifier = Modifier.fillMaxWidth().testTag("product_input_name")
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = priceStr,
                        onValueChange = { priceStr = it },
                        label = { Text("Price ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("product_input_price")
                    )

                    OutlinedTextField(
                        value = discountPriceStr,
                        onValueChange = { discountPriceStr = it },
                        label = { Text("Offer Price ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("product_input_discount")
                    )
                }

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth().testTag("product_input_category")
                )

                OutlinedTextField(
                    value = imageUri,
                    onValueChange = { imageUri = it },
                    label = { Text("Main Image URL") },
                    placeholder = { Text("https://...") },
                    modifier = Modifier.fillMaxWidth().testTag("product_input_image")
                )

                OutlinedTextField(
                    value = additionalImages,
                    onValueChange = { additionalImages = it },
                    label = { Text("Additional Images (comma separated)") },
                    modifier = Modifier.fillMaxWidth().testTag("product_input_extra_images")
                )

                OutlinedTextField(
                    value = stockStr,
                    onValueChange = { stockStr = it },
                    label = { Text("Stock Quantity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("product_input_stock")
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth().height(100.dp).testTag("product_input_desc")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Active Status")
                    Switch(checked = isActive, onCheckedChange = { isActive = it }, modifier = Modifier.testTag("product_input_active_switch"))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Featured Product")
                    Switch(checked = isFeatured, onCheckedChange = { isFeatured = it }, modifier = Modifier.testTag("product_input_featured_switch"))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = priceStr.toDoubleOrNull() ?: 0.0
                    val discountPrice = discountPriceStr.toDoubleOrNull()
                    val stock = stockStr.toIntOrNull() ?: 0
                    if (name.isNotBlank() && price > 0) {
                        onSave(name, price, discountPrice, category, imageUri, additionalImages, description, stock, isActive, isFeatured)
                    }
                },
                modifier = Modifier.testTag("save_product_button")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

fun Modifier.scale(scale: Float): Modifier = this
