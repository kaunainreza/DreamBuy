package com.example.rodtestapp.ui.theme.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.rodtestapp.ui.theme.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(cartVm: CartViewModel = viewModel(), onBack: () -> Unit = {}) {
    val items by cartVm.items.collectAsState()

    // compute totals
    val totalQuantity = items.sumOf { it.quantity }
    val totalPrice = items.sumOf { (it.quantity * it.price) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Cart") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )

        if (items.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Your cart is empty")
            }
            return
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 88.dp) // leave space for bottom bar
        ) {
            items(items) { item ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AsyncImage(
                            model = item.imageUrl,
                            contentDescription = item.title,
                            modifier = Modifier.size(72.dp)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.title, style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Price: ₹${String.format("%.2f", item.price)}", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                "Item total: ₹${String.format("%.2f", item.price * item.quantity)}",
                                style = MaterialTheme.typography.bodySmall
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { cartVm.updateQuantity(item.productId, item.quantity - 1) }
                                ) {
                                    Text("-")
                                }

                                Text(" ${item.quantity} ", modifier = Modifier.padding(horizontal = 8.dp))

                                IconButton(
                                    onClick = { cartVm.updateQuantity(item.productId, item.quantity + 1) }
                                ) {
                                    Text("+")
                                }
                            }
                        }

                        IconButton(onClick = { cartVm.removeItem(item.productId) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Remove")
                        }
                    }
                }
            }
        }

        // Bottom summary bar
        Surface(
            tonalElevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Total items: $totalQuantity", style = MaterialTheme.typography.bodyMedium)
                    Text("Total: ₹${String.format("%.2f", totalPrice)}", style = MaterialTheme.typography.titleMedium)
                }
                Button(onClick = { /* TODO: navigate to checkout or perform action */ }) {
                    Text("Checkout")
                }
            }
        }
    }
}
