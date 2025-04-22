package com.ujikom.crownia.pages

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat

@Composable
fun FavoritePage(modifier: Modifier = Modifier,navController: NavController) {
    val favorites = remember { mutableStateListOf<FavoriteItem>() }
    val context = LocalContext.current
    val user = FirebaseAuth.getInstance().currentUser
    val uid = user?.uid

    LaunchedEffect(uid) {
        if (uid != null) {
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("favorites")
                .whereEqualTo("userId", uid)
                .get()
                .addOnSuccessListener { result ->
                    favorites.clear()
                    for (doc in result) {
                        val item = FavoriteItem(
                            id = doc.id,
                            title = doc.getString("title") ?: "",
                            price = doc.getDouble("price") ?: 0.0,
                            actualPrice = doc.getDouble("actualPrice") ?: 0.0,
                            discount = doc.getDouble("discount") ?: 0.0,
                            imageUrl = (doc.get("images") as? List<String>)?.firstOrNull() ?: ""
                        )
                        favorites.add(item)
                    }
                }
        }
    }

    if (favorites.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Belum ada produk favorit", fontSize = 16.sp)
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                Text(
                    "Favorit Saya",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            items(favorites) { item ->
                FavoriteCard(item = item, onDelete = {
                    val db = FirebaseFirestore.getInstance()
                    db.collection("favorites").document(item.id).delete()
                        .addOnSuccessListener {
                            favorites.remove(item)
                            Toast.makeText(context, "Dihapus dari favorit", Toast.LENGTH_SHORT).show()
                        }
                })
            }
        }
    }
}

@Composable
fun FavoriteCard(item: FavoriteItem, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.title,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, fontWeight = FontWeight.Bold, maxLines = 2)
                Text(
                    text = formatRupiah(item.price),
                    fontSize = 12.sp,
                    style = TextStyle(textDecoration = TextDecoration.LineThrough)
                )
                Text(
                    text = formatRupiah(item.actualPrice),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = { onDelete() }) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Hapus dari favorit",
                    tint = Color.Red
                )
            }
        }
    }
}

fun formatRupiah(number: Double): String {
    val localeID = java.util.Locale("in", "ID")
    val numberFormat = NumberFormat.getCurrencyInstance(localeID)
    return numberFormat.format(number).replace(",00", "")
}
