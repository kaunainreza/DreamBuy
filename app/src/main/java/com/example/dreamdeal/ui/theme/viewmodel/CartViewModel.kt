package com.example.dreamdeal.ui.theme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dreamdeal.ui.theme.data.CartItem
import com.example.dreamdeal.ui.theme.data.CartPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CartViewModel(private val cartPreferences: CartPreferences) : ViewModel() {
    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    val items: StateFlow<List<CartItem>> = _items

    init {
        viewModelScope.launch {
            cartPreferences.cartItems.collectLatest { savedItems ->
                _items.value = savedItems
            }
        }
    }

    private fun saveItems(items: List<CartItem>) {
        viewModelScope.launch {
            cartPreferences.saveCartItems(items)
        }
    }

    fun addToCart(item: CartItem) {
        val existing = _items.value.toMutableList()
        val idx = existing.indexOfFirst { it.productId == item.productId }
        if (idx >= 0) {
            val old = existing[idx]
            existing[idx] = old.copy(quantity = old.quantity + item.quantity)
        } else {
            existing.add(item)
        }
        saveItems(existing)
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
            saveItems(existing)
        }
    }

    fun removeItem(productId: Int) {
        val existing = _items.value.toMutableList()
        val idx = existing.indexOfFirst { it.productId == productId }
        if (idx >= 0) {
            existing.removeAt(idx)
            saveItems(existing)
        }
    }

    fun clearCart() {
        saveItems(emptyList())
    }
}
