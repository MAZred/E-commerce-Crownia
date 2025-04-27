package com.ujikom.Ishi_Shop.components

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ujikom.Ishi_Shop.model.ProductModel

@Composable
fun ProductItemView(modifier: Modifier = Modifier, product: ProductModel,navController: NavController) {
    val context = LocalContext.current
    var isFavorited by remember { mutableStateOf(false) } // State untuk toggle love
    val currentUser = FirebaseAuth.getInstance().currentUser
    val uid = currentUser?.uid

    Card(
        modifier = modifier
            .padding(vertical = 8.dp)
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = product.images.firstOrNull(),
                contentDescription = product.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            )

            // Tombol love di pojok kanan atas gambar
            IconButton(
                onClick = {
                    if (uid == null) {
                        navController.navigate("auth")
                    } else {
                        isFavorited = true // langsung true, atau bisa toggle
                        val db = FirebaseFirestore.getInstance()
                        val favoriteItem = hashMapOf(
                            "userId" to uid,
                            "title" to product.title,
                            "actualPrice" to product.actualPrice,
                            "discount" to product.discount,
                            "images" to product.images
                        )

                        db.collection("favorites")
                            .add(favoriteItem)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Ditambahkan ke favorit", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Gagal menambahkan", Toast.LENGTH_SHORT).show()
                            }
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(32.dp)
                    .background(Color.White.copy(alpha = 0.8f), shape = CircleShape)
            ) {
                Icon(
                    imageVector = if (isFavorited) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorited) Color.Red else Color.Gray
                )
            }
        }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = product.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Rp ${"%,.0f".format(product.actualPrice)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = {
                            val currentUser = FirebaseAuth.getInstance().currentUser
                            if (currentUser == null) {
                                // Redirect ke halaman auth kalau belum login
                                navController.navigate("auth")
                            } else {
                                // User sudah login, bisa langsung add to cart
                                addToCart(product, context)
                            }
                        },
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Add to cart",
                            tint = Color.White
                        )
                    }

                }
            }
        }
    }


fun addToCart(product: ProductModel, context: Context) {
    val firebaseUser = FirebaseAuth.getInstance().currentUser
    if (firebaseUser == null) {
        Log.e("Cart", "User belum login")
        Toast.makeText(context, "Anda belum login", Toast.LENGTH_SHORT).show()
        return
    }
    val uid = firebaseUser.uid
    val firestore = FirebaseFirestore.getInstance()
    val cartRef = firestore.collection("cart")

    cartRef
        .whereEqualTo("userId", uid)
        .whereEqualTo("productId", product.id)
        .get()
        .addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                // Jika produk sudah ada, update quantity dengan menambah 1
                for (document in querySnapshot.documents) {
                    val currentQuantity = document.getLong("quantity")?.toInt() ?: 1
                    document.reference.update("quantity", currentQuantity + 1)
                        .addOnSuccessListener {
                            Log.d("Cart", "Quantity berhasil diupdate")
                        }
                        .addOnFailureListener { e ->
                            Log.e("Cart", "Gagal update quantity: ${e.message}")
                        }
                }
            } else {
                // Simpan data baru ke Firestore dengan price, discount, dan quantity sebagai number
                val newCartItem = hashMapOf(
                    "userId" to uid,
                    "productId" to product.id,
                    "productName" to product.title,
                    "price" to product.actualPrice,
                    "discount" to product.discount,
                    "quantity" to 1,
                    "imageUrl" to (product.images.firstOrNull() ?: "")
                )

                cartRef.add(newCartItem)
                    .addOnSuccessListener {
                        Log.d("Cart", "Produk berhasil ditambahkan ke cart")
                    }
                    .addOnFailureListener { e ->
                        Log.e("Cart", "Gagal menambahkan produk ke cart: ${e.message}")
                    }
            }
            // Tampilkan notifikasi setelah berhasil menambahkan ke cart
            Toast.makeText(context, "Produk berhasil ditambahkan ke cart", Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener { e ->
            Log.e("Cart", "Gagal mengambil data cart: ${e.message}")
            Toast.makeText(context, "Gagal menambahkan produk ke cart", Toast.LENGTH_SHORT).show()
        }
}
