package com.example.dreamdeal.ui.theme.ui

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.dreamdeal.ui.theme.RodTestAppTheme
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

    // Confirmation message state
    var showConfirm by remember { mutableStateOf(false) }
    var confirmText by remember { mutableStateOf("") }

    ProductDetailContent(
        product = product,
        inCartQty = inCartQty,
        cartCount = cartItems.count { it.quantity > 0 },
        showConfirm = showConfirm,
        confirmText = confirmText,
        onBack = onBack,
        onOpenCart = onOpenCart,
        onUpdateQuantity = { qty -> cartVm.updateQuantity(productId, qty) },
        onAddToCart = { cartVm.addToCart(CartItem.fromProduct(product, 1)) },
        onResetConfirm = { showConfirm = false },
        onShowConfirm = { text ->
            confirmText = text
            showConfirm = true
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailContent(
    product: Product,
    inCartQty: Int,
    cartCount: Int,
    showConfirm: Boolean,
    confirmText: String,
    onBack: () -> Unit,
    onOpenCart: () -> Unit,
    onUpdateQuantity: (Int) -> Unit,
    onAddToCart: () -> Unit,
    onResetConfirm: () -> Unit,
    onShowConfirm: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {

        TopAppBar(
            title = { Text(text = product.title, maxLines = 1) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                CartIcon(
                    count = cartCount,
                    onOpenCart = onOpenCart
                )
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

            Spacer(modifier = Modifier.height(24.dp))

            // Cart interaction section - matching HomeScreen behavior logic
            if (inCartQty > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            val newQty = inCartQty - 1
                            onUpdateQuantity(newQty)
                            onShowConfirm(if (newQty > 0) "Updated quantity to $newQty" else "Removed from cart")
                        },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("-")
                    }

                    Text(
                        text = inCartQty.toString(),
                        modifier = Modifier.padding(horizontal = 24.dp),
                        style = MaterialTheme.typography.titleLarge
                    )

                    Button(
                        onClick = {
                            val newQty = (inCartQty + 1).coerceAtMost(99)
                            onUpdateQuantity(newQty)
                            onShowConfirm("Updated quantity to $newQty")
                        },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("+")
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            onAddToCart()
                            onShowConfirm("Added to cart")
                        },
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "Add to Cart",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            if (showConfirm) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(confirmText, style = MaterialTheme.typography.bodyMedium)
                }
                // auto-hide after a short delay
                LaunchedEffect(confirmText) {
                    delay(1500)
                    onResetConfirm()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductDetailPreview() {
    RodTestAppTheme {
        ProductDetailContent(
            product = Product(1, "Sample Product", 99.0, 4.5, ""),
            inCartQty = 2,
            cartCount = 1,
            showConfirm = false,
            confirmText = "",
            onBack = {},
            onOpenCart = {},
            onUpdateQuantity = {},
            onAddToCart = {},
            onResetConfirm = {},
            onShowConfirm = {}
        )
    }
}
