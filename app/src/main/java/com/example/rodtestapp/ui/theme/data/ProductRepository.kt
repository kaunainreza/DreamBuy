package com.example.rodtestapp.ui.theme.data

import com.example.rodtestapp.ui.theme.network.RetrofitClient


class ProductRepository {

    suspend fun fetchProducts(): List<Product> {
        val res = RetrofitClient.api.getProducts()
        return res.products.map { it.toProduct() }
    }

    suspend fun searchProducts(query: String): List<Product> {
        val res = RetrofitClient.api.searchProducts(query)
        return res.products.map { it.toProduct() }
    }
}

private fun ProductDto.toProduct(): Product =
    Product(
        id = id,
        title = title,
        price = price,
        rating = rating,
        imageUrl = thumbnail
    )