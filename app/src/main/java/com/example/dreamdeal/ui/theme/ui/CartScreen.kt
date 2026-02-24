package com.example.dreamdeal.ui.theme.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.dreamdeal.ui.theme.RodTestAppTheme
import com.example.dreamdeal.ui.theme.data.CartItem
import com.example.dreamdeal.ui.theme.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(cartVm: CartViewModel = viewModel(), onBack: () -> Unit = {}) {
    val items by cartVm.items.collectAsState()
    
    val subtotal = items.sumOf { it.quantity * it.price }
    val gst = subtotal * 0.05
    val surgeFee = if (items.isNotEmpty()) 9.0 else 0.0
    val total = subtotal + gst + surgeFee

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cart", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            if (items.isNotEmpty()) {
                CartBottomBar(total = total)
            }
        }
    ) { innerPadding ->
        CartContent(
            items = items,
            subtotal = subtotal,
            gst = gst,
            surgeFee = surgeFee,
            total = total,
            onUpdateQuantity = { id, qty -> cartVm.updateQuantity(id, qty) },
            onRemoveItem = { id -> cartVm.removeItem(id) },
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun CartContent(
    items: List<CartItem>,
    subtotal: Double,
    gst: Double,
    surgeFee: Double,
    total: Double,
    onUpdateQuantity: (Int, Int) -> Unit,
    onRemoveItem: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (items.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Your cart is empty", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
    ) {
        items(items) { item ->
            CartItemCard(
                item = item,
                onUpdateQuantity = onUpdateQuantity,
                onRemoveItem = onRemoveItem
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
            
            BillDetails(
                subtotal = subtotal,
                gst = gst,
                surgeFee = surgeFee,
                total = total
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun CartItemCard(
    item: CartItem,
    onUpdateQuantity: (Int, Int) -> Unit,
    onRemoveItem: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.title,
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .padding(4.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
                Text(
                    "₹${String.format("%.2f", item.price)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    OutlinedIconButton(
                        onClick = { onUpdateQuantity(item.productId, item.quantity - 1) },
                        modifier = Modifier.size(32.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("-", fontWeight = FontWeight.Bold)
                    }

                    Text(
                        "${item.quantity}",
                        modifier = Modifier.padding(horizontal = 12.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )

                    OutlinedIconButton(
                        onClick = { onUpdateQuantity(item.productId, item.quantity + 1) },
                        modifier = Modifier.size(32.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("+", fontWeight = FontWeight.Bold)
                    }
                }
            }

            IconButton(onClick = { onRemoveItem(item.productId) }) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Remove",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun BillDetails(
    subtotal: Double,
    gst: Double,
    surgeFee: Double,
    total: Double
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            "Bill Details",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        BillRow("Item Total", subtotal)
        BillRow("GST (5%)", gst)
        BillRow("Surge Fee", surgeFee)
        
        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "To Pay",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                "₹${String.format("%.2f", total)}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun BillRow(label: String, amount: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Text("₹${String.format("%.2f", amount)}", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun CartBottomBar(total: Double) {
    Surface(
        shadowElevation = 16.dp,
        tonalElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    "To Pay",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
                Text(
                    "₹${String.format("%.2f", total)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Button(
                onClick = { /* Handle Payment */ },
                modifier = Modifier
                    .height(50.dp)
                    .width(160.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "Pay Now",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CartScreenPreview() {
    val mockItems = listOf(
        CartItem(1, "Nike Air Max", 1299.0, 1, ""),
        CartItem(2, "Adidas Ultraboost", 2499.0, 2, "")
    )
    val subtotal = mockItems.sumOf { it.quantity * it.price }
    val gst = subtotal * 0.05
    val surgeFee = 9.0
    val total = subtotal + gst + surgeFee

    RodTestAppTheme {
        Scaffold(
            bottomBar = { CartBottomBar(total = total) }
        ) { padding ->
            CartContent(
                items = mockItems,
                subtotal = subtotal,
                gst = gst,
                surgeFee = surgeFee,
                total = total,
                onUpdateQuantity = { _, _ -> },
                onRemoveItem = { _ -> },
                modifier = Modifier.padding(padding)
            )
        }
    }
}
