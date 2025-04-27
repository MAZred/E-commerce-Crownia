package com.ujikom.crownia.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import com.ujikom.crownia.pages.CartItem

@Composable
fun SummaryRow(
    label: String,
    value: String,
    fontWeight: FontWeight = FontWeight.Normal,
    valueColor: Color = Color.Unspecified
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = fontWeight)
        Text(value, fontWeight = fontWeight, color = valueColor)
    }
}

@Composable
fun CartItemCard(
    item: CartItem,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Checkbox(checked = isChecked, onCheckedChange = onCheckedChange)

            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.title,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, fontWeight = FontWeight.SemiBold, fontSize = 150.sp)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Rp ${"%,.0f".format(item.price)}",
                    color = colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        val newQty = item.quantity - 1
                        val cartRef = FirebaseFirestore.getInstance().collection("cart").document(item.id)
                        if (newQty > 0) cartRef.update("quantity", newQty) else cartRef.delete()
                    }) {
                        Icon(Icons.Default.Remove, contentDescription = "Kurangi")
                    }
                    Text("${item.quantity}", modifier = Modifier.padding(horizontal = 8.dp))
                    IconButton(onClick = {
                        FirebaseFirestore.getInstance().collection("cart").document(item.id)
                            .update("quantity", item.quantity + 1)
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Tambah")
                    }
                }
            }
        }
    }
}
