package com.example.dreamdeal.ui.theme.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartIcon(
    count: Int,
    onOpenCart: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    IconButton(onClick = { onOpenCart() }, modifier = modifier) {
        BadgedBox(badge = {
            if (count > 0) {
                Badge { Text(count.toString()) }
            }
        }) {
            Icon(Icons.Filled.ShoppingCart, contentDescription = "Cart")
        }
    }
}
