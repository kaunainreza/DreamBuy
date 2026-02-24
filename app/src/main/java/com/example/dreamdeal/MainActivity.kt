package com.example.dreamdeal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dreamdeal.ui.theme.RodTestAppTheme
import com.example.dreamdeal.ui.theme.data.CartPreferences
import com.example.dreamdeal.ui.theme.ui.CartScreen
import com.example.dreamdeal.ui.theme.ui.HomeScreen
import com.example.dreamdeal.ui.theme.ui.ProductDetailScreen
import com.example.dreamdeal.ui.theme.viewmodel.CartViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RodTestAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNav(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

class CartViewModelFactory(private val context: android.content.Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartViewModel(CartPreferences(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@Composable
fun AppNav(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val context = LocalContext.current

    // activity-scoped CartViewModel so all screens share the same cart state
    val cartVm: CartViewModel = viewModel(
        factory = CartViewModelFactory(context.applicationContext)
    )

    NavHost(navController = navController, startDestination = "home", modifier = modifier) {
        composable("home") {
            HomeScreen(
                modifier = Modifier,
                cartVm = cartVm,
                onProductClick = { id -> navController.navigate("detail/$id") },
                onOpenCart = { navController.navigate("cart") }
            )
        }

        composable("detail/{productId}") { backStackEntry ->
            val idStr = backStackEntry.arguments?.getString("productId")
            val id = idStr?.toIntOrNull()
            if (id == null) {
                // show a simple not found UI
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Invalid product")
                }
            } else {
                ProductDetailScreen(
                    productId = id,
                    cartVm = cartVm,
                    onBack = { navController.popBackStack() },
                    onOpenCart = { navController.navigate("cart") }
                )
            }
        }

        composable("cart") {
            CartScreen(cartVm = cartVm, onBack = { navController.popBackStack() })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    RodTestAppTheme {
        HomeScreen(modifier = Modifier)
    }
}