package com.example.rodtestapp.ui.theme.ui


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.rodtestapp.ui.theme.data.Product

@Composable
fun ProductCard(product: Product, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(10.dp)) {

            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(8.dp))
            Text(product.title, maxLines = 2)
            Text("₹${product.price}", style = MaterialTheme.typography.titleMedium)
            Text("⭐ ${product.rating}")
        }
    }
}