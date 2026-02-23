package com.example.rodtestapp.ui.theme.network

import com.example.rodtestapp.ui.theme.data.ProductDto
import com.example.rodtestapp.ui.theme.data.ProductsResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("products")
    suspend fun getProducts(): ProductsResponse

    @GET("products/search")
    suspend fun searchProducts(
        @Query("q") query: String
    ): ProductsResponse

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Int): ProductDto
}