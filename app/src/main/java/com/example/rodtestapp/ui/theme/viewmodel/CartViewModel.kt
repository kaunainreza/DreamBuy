package com.example.rodtestapp.ui.theme.viewmodel

import androidx.lifecycle.ViewModel
import com.example.rodtestapp.ui.theme.data.CartItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CartViewModel : ViewModel() {
    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    val items: StateFlow<List<CartItem>> = _items

    fun addToCart(item: CartItem) {
        val existing = _items.value.toMutableList()
        val idx = existing.indexOfFirst { it.productId == item.productId }
        if (idx >= 0) {
            val old = existing[idx]
            existing[idx] = old.copy(quantity = old.quantity + item.quantity)
        } else {
            existing.add(item)
        }
        _items.value = existing
    }

    fun updateQuantity(productId: Int, quantity: Int) {
        val existing = _items.value.toMutableList()
        val idx = existing.indexOfFirst { it.productId == productId }
        if (idx >= 0) {
            if (quantity <= 0) {
                existing.removeAt(idx)
            } else {
                val old = existing[idx]
                existing[idx] = old.copy(quantity = quantity)
            }
            _items.value = existing
        }
    }

    fun removeItem(productId: Int) {
        val existing = _items.value.toMutableList()
        val idx = existing.indexOfFirst { it.productId == productId }
        if (idx >= 0) {
            existing.removeAt(idx)
            _items.value = existing
        }
    }

    fun clearCart() {
        _items.value = emptyList()
    }
}
