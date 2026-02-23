package com.example.rodtestapp.ui.theme.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rodtestapp.ui.theme.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartIconWithPreview(
    cartVm: CartViewModel = viewModel(),
    onOpenCart: () -> Unit = {}
) {
    val items by cartVm.items.collectAsState()
    val count = items.sumOf { it.quantity }

    IconButton(onClick = { onOpenCart() }) {
        BadgedBox(badge = {
            if (count > 0) {
                Badge { Text(count.toString()) }
            }
        }) {
            Icon(Icons.Filled.ShoppingCart, contentDescription = "Cart")
        }
    }
}
