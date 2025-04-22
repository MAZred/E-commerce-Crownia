package com.ujikom.crownia.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class CartItem(
    var id: String = "",        // sekarang ada default value
    var title: String = "",
    var price: Double = 0.0,
    var actualPrice: Double = 0.0,
    var discount: Double = 0.0,
    var quantity: Int = 0,
    var imageUrl: String = ""
)



@Composable
fun CartItemCard(
    modifier: Modifier = Modifier,
    item: CartItem,
    onDelete: (String) -> Unit // Callback untuk menghapus item berdasarkan document id
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, fontWeight = FontWeight.Bold)
                Text("Harga: Rp ${item.price}")
                Text("Diskon: ${item.discount}%")
                Text("Jumlah: ${item.quantity}")
            }
            IconButton(onClick = { onDelete(item.id) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Hapus item"
                )
            }
        }
    }
}


