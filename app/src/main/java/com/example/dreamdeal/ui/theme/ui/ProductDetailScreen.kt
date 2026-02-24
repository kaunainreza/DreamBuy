package com.example.dreamdeal.ui.theme.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.dreamdeal.ui.theme.data.CartItem
import com.example.dreamdeal.ui.theme.data.Product
import com.example.dreamdeal.ui.theme.viewmodel.CartViewModel
import com.example.dreamdeal.ui.theme.viewmodel.ShopViewModel
import kotlinx.coroutines.delay

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

    // Quantity state for cart selector - default to 1 so auto-add is obvious
    val qtyState = remember { mutableStateOf(1) }

    // Confirmation message state (moved above LaunchedEffect so we can set it there)
    var showConfirm by remember { mutableStateOf(false) }
    var confirmText by remember { mutableStateOf("") }

    // ensure selector resets each time this product screen is (re)entered
    // and auto-add the product to the cart when the screen opens (only if not already in cart)
    LaunchedEffect(productId) {
        // read current cart snapshot and initialize selector based on it
        val existing = cartItems.find { it.productId == productId }
        if (existing != null) {
            qtyState.value = existing.quantity
        } else {
            qtyState.value = 0
            // only add to cart once when the screen is first opened for this product
            val cartItem = CartItem.fromProduct(product, qtyState.value)
            cartVm.addToCart(cartItem)
            confirmText = "Added ${qtyState.value} to cart"
            showConfirm = true
        }
    }

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
                CartIconWithPreview(
                    count = cartItems.sumOf { it.quantity },
                    onOpenCart = onOpenCart
                )            }
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
                    onClick = {
                        val newQty = (qtyState.value - 1).coerceAtLeast(0)
                        qtyState.value = newQty
                        // if item exists update/remove, otherwise if newQty>0 add
                        val exists = cartItems.any { it.productId == productId }
                        if (exists) {
                            cartVm.updateQuantity(productId, newQty)
                            confirmText = if (newQty > 0) "Updated quantity to $newQty" else "Removed from cart"
                        } else if (newQty > 0) {
                            cartVm.addToCart(CartItem.fromProduct(product, newQty))
                            confirmText = "Added $newQty to cart"
                        }
                        showConfirm = true
                    },
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
                    onClick = {
                        val newQty = (qtyState.value + 1).coerceAtMost(99)
                        qtyState.value = newQty
                        val exists = cartItems.any { it.productId == productId }
                        if (exists) {
                            cartVm.updateQuantity(productId, newQty)
                            confirmText = "Updated quantity to $newQty"
                        } else {
                            cartVm.addToCart(CartItem.fromProduct(product, newQty))
                            confirmText = "Added $newQty to cart"
                        }
                        showConfirm = true
                    },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("+")
                }

                // Removed explicit "Add to Cart" button — product is added automatically when this screen opens
            }

            if (showConfirm) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(confirmText, style = MaterialTheme.typography.bodyMedium)
                // auto-hide after a short delay
                LaunchedEffect(confirmText) {
                    delay(1500)
                    showConfirm = false
                }
            }
        }
    }
}

