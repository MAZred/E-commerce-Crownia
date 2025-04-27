package com.ujikom.Ishi_Shop.pages


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.ujikom.Ishi_Shop.components.BannerView
import com.ujikom.Ishi_Shop.components.CategoriesView
import com.ujikom.Ishi_Shop.components.HeaderView
import com.ujikom.Ishi_Shop.components.ProductItemView
import com.ujikom.Ishi_Shop.model.ProductModel

@Composable
fun HomePage(modifier: Modifier = Modifier, navController: NavController) {
    val products = remember { mutableStateListOf<ProductModel>() }

    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("data")
            .document("stock")
            .collection("products")
            .get()
            .addOnSuccessListener { result ->
                products.clear()
                for (document in result) {
                    val product = ProductModel(
                        id = document.id,
                        title = document.getString("title") ?: "",
                        description = document.getString("description") ?: "",
                        price = document.getDouble("price") ?: 0.0,
                        actualPrice = document.getDouble("actualPrice") ?: 0.0,
                        discount = document.getDouble("discount") ?: 0.0,
                        category = document.getString("category") ?: "",
                        images = document.get("images") as? List<String> ?: emptyList()
                    )
                    products.add(product)
                }
            }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item {
            HeaderView(modifier = Modifier.fillMaxWidth(), navController = navController)
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            BannerView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                "Kategori",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            CategoriesView()
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                "Produk Terbaru",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(products) { product ->
            ProductItemView(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                product = product,
                navController = navController
            )
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

