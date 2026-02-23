package com.example.rodtestapp.ui.theme.ui

import com.example.rodtestapp.ui.theme.viewmodel.ShopViewModel


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rodtestapp.ui.theme.RodTestAppTheme
import com.example.rodtestapp.ui.theme.data.Product
import com.example.rodtestapp.ui.theme.viewmodel.CartViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    vm: ShopViewModel = viewModel(),
    cartVm: CartViewModel = viewModel(),
    onProductClick: (Int) -> Unit = {},
    onOpenCart: () -> Unit = {}
) {

    val products by vm.products.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()

    HomeScreenContent(
        products = products,
        loading = loading,
        error = error,
        onSearch = { vm.search(it) },
        onProductClick = onProductClick,
        onOpenCart = onOpenCart,
        cartVm = cartVm,
        modifier = modifier
    )
}

@Composable
fun HomeScreenContent(
    products: List<Product>,
    loading: Boolean,
    error: String?,
    onSearch: (String) -> Unit,
    onProductClick: (Int) -> Unit,
    onOpenCart: () -> Unit,
    cartVm: CartViewModel,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxSize()) {

        // Top row: search field and cart icon aligned to top
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    onSearch(it)
                },
                modifier = Modifier
                    .weight(1f),
                placeholder = { Text("Search products…") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true
            )

            Spacer(modifier = Modifier.width(8.dp))

            CartIconWithPreview(onOpenCart = onOpenCart, cartVm = cartVm)
        }

        when {
            loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(error)
            }

            else -> LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(products) { product ->
                    ProductCard(product) { onProductClick(product.id) }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    RodTestAppTheme {
        HomeScreenContent(
            products = listOf(
                Product(1, "Fjallraven - Foldsack No. 1 Backpack", 109.0, 3.9, ""),
                Product(2, "Mens Casual Premium Slim Fit T-Shirts ", 22.5, 4.1, ""),
                Product(3, "Mens Cotton Jacket", 55.5, 4.7, ""),
                Product(4, "Mens Casual Slim Fit", 15.99, 2.1, "")
            ),
            loading = false,
            error = null,
            onSearch = {},
            onProductClick = {},
            onOpenCart = {},
            cartVm = viewModel(),
            modifier = Modifier
        )
    }
}
