package com.example.rodtestapp.ui.theme.viewmodel


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rodtestapp.ui.theme.data.Product
import com.example.rodtestapp.ui.theme.data.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ShopViewModel(
    private val repo: ProductRepository = ProductRepository()
) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Local fallback sample data to display when network fails
    private val sampleProducts = listOf(
        Product(id = 1, title = "Sample Product A", price = 99.0, rating = 4.5, imageUrl = ""),
        Product(id = 2, title = "Sample Product B", price = 149.1, rating = 4.0, imageUrl = ""),
        Product(id = 3, title = "Sample Product C", price = 79.5, rating = 3.8, imageUrl = "")
    )

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                _products.value = repo.fetchProducts()
            } catch (e: Exception) {
                Log.e("ShopViewModel", "Failed to load products", e)
                // Provide a user-friendly error message and fall back to sample data
                _error.value = "Failed to load products: ${e.message ?: "unknown error"}"
                _products.value = sampleProducts
            } finally {
                _loading.value = false
            }
        }
    }

    fun search(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                loadProducts()
                return@launch
            }
            _loading.value = true
            _error.value = null
            try {
                _products.value = repo.searchProducts(query)
            } catch (e: Exception) {
                Log.e("ShopViewModel", "Search failed", e)
                _error.value = "Search failed: ${e.message ?: "unknown error"}"
                // Keep existing list or fall back to sample data if empty
                if (_products.value.isEmpty()) _products.value = sampleProducts
            } finally {
                _loading.value = false
            }
        }
    }
}