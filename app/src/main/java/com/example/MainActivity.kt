package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.TopBarHeader
import com.example.ui.screens.*
import com.example.ui.theme.ShopSphereTheme
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.ShopViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShopSphereTheme {
                ShopAppContent()
            }
        }
    }
}

@Composable
fun ShopAppContent(viewModel: ShopViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val isAdminMode by viewModel.isAdminMode.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val sortOption by viewModel.sortOption.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()

    val filteredProducts by viewModel.filteredCustomerProducts.collectAsState()
    val allProducts by viewModel.allProducts.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val cartCount by viewModel.cartCount.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val cartSubtotal by viewModel.cartSubtotal.collectAsState()
    val deliveryFee by viewModel.deliveryFee.collectAsState()
    val cartTotal by viewModel.cartTotal.collectAsState()
    val allOrders by viewModel.allOrders.collectAsState()
    val allCustomers by viewModel.allCustomers.collectAsState()

    val totalSales by viewModel.totalSales.collectAsState()
    val pendingOrdersCount by viewModel.pendingOrdersCount.collectAsState()
    val completedOrdersCount by viewModel.completedOrdersCount.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBarHeader(
                appName = "ShopSphere",
                isAdminMode = isAdminMode,
                cartCount = cartCount,
                searchQuery = searchQuery,
                onSearchQueryChange = viewModel::setSearchQuery,
                onCartClick = { viewModel.navigateTo(Screen.Cart) },
                onOrdersClick = { viewModel.navigateTo(Screen.MyOrders) },
                onAdminToggle = viewModel::toggleAdminMode,
                onHomeClick = {
                    if (isAdminMode) viewModel.navigateTo(Screen.AdminDashboard)
                    else viewModel.navigateTo(Screen.Home)
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val screen = currentScreen) {
                is Screen.Home -> {
                    HomeScreen(
                        products = filteredProducts,
                        categories = categories,
                        selectedCategory = selectedCategory,
                        sortOption = sortOption,
                        onCategorySelect = viewModel::setSelectedCategory,
                        onSortOptionSelect = viewModel::setSortOption,
                        onProductClick = { prodId -> viewModel.navigateTo(Screen.ProductDetail(prodId)) },
                        onAddToCart = { prodId -> viewModel.addToCart(prodId) },
                        onBuyNow = { prodId -> viewModel.buyNow(prodId) }
                    )
                }

                is Screen.ProductDetail -> {
                    val product = allProducts.find { it.id == screen.productId }
                    ProductDetailScreen(
                        product = product,
                        allProducts = allProducts,
                        onBackClick = { viewModel.navigateTo(Screen.Home) },
                        onAddToCart = { prodId, qty -> viewModel.addToCart(prodId, qty) },
                        onBuyNow = { prodId, qty -> viewModel.buyNow(prodId, qty) },
                        onProductClick = { prodId -> viewModel.navigateTo(Screen.ProductDetail(prodId)) }
                    )
                }

                is Screen.Cart -> {
                    CartScreen(
                        cartItems = cartItems,
                        subtotal = cartSubtotal,
                        deliveryFee = deliveryFee,
                        total = cartTotal,
                        onBackClick = { viewModel.navigateTo(Screen.Home) },
                        onUpdateQuantity = viewModel::updateCartQuantity,
                        onRemoveItem = viewModel::removeFromCart,
                        onProceedToCheckout = { viewModel.navigateTo(Screen.Checkout) },
                        onExploreProducts = { viewModel.navigateTo(Screen.Home) }
                    )
                }

                is Screen.Checkout -> {
                    CheckoutScreen(
                        cartItems = cartItems,
                        subtotal = cartSubtotal,
                        deliveryFee = deliveryFee,
                        total = cartTotal,
                        onBackClick = { viewModel.navigateTo(Screen.Cart) },
                        onPlaceOrder = viewModel::placeOrder
                    )
                }

                is Screen.MyOrders -> {
                    OrdersScreen(
                        orders = allOrders,
                        onBackClick = { viewModel.navigateTo(Screen.Home) },
                        onCancelOrder = { orderId -> viewModel.updateOrderStatus(orderId, com.example.data.model.OrderStatus.CANCELLED) }
                    )
                }

                is Screen.AdminDashboard -> {
                    AdminDashboardScreen(
                        totalProducts = allProducts.size,
                        totalCustomers = allCustomers.size,
                        totalOrders = allOrders.size,
                        pendingOrders = pendingOrdersCount,
                        completedOrders = completedOrdersCount,
                        totalSales = totalSales,
                        onNavigateToProducts = { viewModel.navigateTo(Screen.AdminProducts) },
                        onNavigateToOrders = { viewModel.navigateTo(Screen.AdminOrders) },
                        onNavigateToCustomers = { viewModel.navigateTo(Screen.AdminCustomers) }
                    )
                }

                is Screen.AdminProducts -> {
                    AdminProductManageScreen(
                        products = allProducts,
                        categories = categories,
                        onBackClick = { viewModel.navigateTo(Screen.AdminDashboard) },
                        onSaveProduct = viewModel::saveProduct,
                        onDeleteProduct = viewModel::deleteProduct
                    )
                }

                is Screen.AdminOrders -> {
                    AdminOrderManageScreen(
                        orders = allOrders,
                        onBackClick = { viewModel.navigateTo(Screen.AdminDashboard) },
                        onUpdateOrderStatus = viewModel::updateOrderStatus
                    )
                }

                is Screen.AdminCustomers -> {
                    AdminCustomerManageScreen(
                        customers = allCustomers,
                        onBackClick = { viewModel.navigateTo(Screen.AdminDashboard) },
                        onToggleBlock = viewModel::toggleCustomerBlock
                    )
                }
            }
        }
    }
}
