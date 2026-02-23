package com.example.rodtestapp.ui.theme.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.rodtestapp.ui.theme.data.CartItem
import com.example.rodtestapp.ui.theme.data.Product
import com.example.rodtestapp.ui.theme.viewmodel.CartViewModel
import com.example.rodtestapp.ui.theme.viewmodel.ShopViewModel
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("DEPRECATION")
@Composable
fun ProductDetailScreen(
    productId: Int,
    vm: ShopViewModel = viewModel(),
    cartVm: CartViewModel = viewModel(),
    onBack: () -> Unit = {},
    onOpenCart: () -> Unit = {}
) {
    val products by vm.products.collectAsState()
    val product: Product? = products.find { it.id == productId }

    if (product == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Product not found")
        }
        return
    }

    // Observe current cart items to show in-cart quantity
    val cartItems by cartVm.items.collectAsState()
    val inCartQty = cartItems.find { it.productId == productId }?.quantity ?: 0

    // Quantity state for cart selector - default to 1 so "Add 1" is obvious
    val qtyState = remember { mutableStateOf(1) }

    // ensure selector resets each time this product screen is (re)entered
    LaunchedEffect(productId) {
        qtyState.value = 1
    }

    // Confirmation message state
    var showConfirm by remember { mutableStateOf(false) }
    var confirmText by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .fillMaxSize()) {

        TopAppBar(
            title = { Text(text = product.title, maxLines = 1) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                CartIconWithPreview(onOpenCart = onOpenCart, cartVm = cartVm)
            }
        )

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(product.title, style = MaterialTheme.typography.titleLarge)
            Text("₹${product.price}", style = MaterialTheme.typography.titleMedium)
            Text("⭐ ${product.rating}", style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(12.dp))
            Text("Product details would go here. Add description, specifications, or actions like Add to Cart.")

            Spacer(modifier = Modifier.height(12.dp))
            // show current in-cart quantity
            Text("In cart: $inCartQty", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))

            // Quantity selector row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = { if (qtyState.value > 0) qtyState.value = qtyState.value - 1 },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("-")
                }

                Text(
                    text = qtyState.value.toString(),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.titleMedium
                )

                Button(
                    onClick = { if (qtyState.value < 99) qtyState.value = qtyState.value + 1 },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("+")
                }

                Spacer(modifier = Modifier.width(24.dp))

                // Add to Cart action (adds the selected amount)
                Button(
                    onClick = {
                        val cartItem = CartItem.fromProduct(product, qtyState.value)
                        cartVm.addToCart(cartItem)
                        confirmText = "Added ${qtyState.value} to cart"
                        // reset the selector to 1 so subsequent adds are intentional
                        qtyState.value = 1
                        showConfirm = true
                    },
                    enabled = qtyState.value > 0
                ) {
                    Text("Add to Cart")
                }
            }

            if (showConfirm) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(confirmText, style = MaterialTheme.typography.bodyMedium)
                // auto-hide after a short delay
                LaunchedEffect(confirmText) {
                    kotlinx.coroutines.delay(1500)
                    showConfirm = false
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductDetailScreenPreview() {
    // Create simple VMs for preview. These are lightweight and safe to instantiate in previews.
    val shopVm = ShopViewModel()
    val cartVm = CartViewModel()

    // Use a known sample product id (ShopViewModel provides sampleProducts with ids 1..3)
    ProductDetailScreen(productId = 1, vm = shopVm, cartVm = cartVm, onBack = {}, onOpenCart = {})
}
