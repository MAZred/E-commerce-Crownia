package com.ujikom.crownia.pages

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.ujikom.crownia.components.TitleTopBar
import com.ujikom.crownia.pages.CartItem

@Composable
fun CartPage(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val cartItems = remember { mutableStateListOf<CartItem>() }
    val selectedIds = remember { mutableStateListOf<String>() }
    val context = LocalContext.current
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    var adminFee by remember { mutableStateOf(2000.0) }
    var shippingFee by remember { mutableStateOf(15000.0) }

    // Load data dari Firestore
    LaunchedEffect(uid) {
        if (uid == null) return@LaunchedEffect
        val db = FirebaseFirestore.getInstance()

        db.collection("cart")
            .whereEqualTo("userId", uid)
            .addSnapshotListener { snap, _ ->
                cartItems.clear()
                snap?.documents?.forEach { doc ->
                    doc.toObject(CartItem::class.java)
                        ?.apply { id = doc.id }
                        ?.let { cartItems += it }
                }
            }

        db.collection("data").document("stock").get()
            .addOnSuccessListener { doc ->
                adminFee = doc.getDouble("adminFee") ?: adminFee
                shippingFee = doc.getDouble("shippingFee") ?: shippingFee
            }
    }

    Scaffold(
        topBar = {
            TitleTopBar(title = "Keranjang") {
                navController.popBackStack()
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(cartItems) { item ->
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
                        Checkbox(
                            checked = selectedIds.contains(item.id),
                            onCheckedChange = {
                                if (it) selectedIds += item.id else selectedIds -= item.id
                            }
                        )

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
                            Text(item.title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                            Spacer(Modifier.height(4.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "Rp ${"%,.0f".format(item.price)}",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }

                            Spacer(Modifier.height(8.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = {
                                    val newQty = item.quantity - 1
                                    val db = FirebaseFirestore.getInstance()
                                    val cartRef = db.collection("cart").document(item.id)

                                    if (newQty > 0) {
                                        cartRef.update("quantity", newQty)
                                    } else {
                                        cartRef.delete()
                                    }
                                }) {
                                    Icon(Icons.Default.Remove, contentDescription = "Kurangi")
                                }

                                Text("${item.quantity}", modifier = Modifier.padding(horizontal = 8.dp))

                                IconButton(onClick = {
                                    FirebaseFirestore.getInstance()
                                        .collection("cart")
                                        .document(item.id)
                                        .update("quantity", item.quantity + 1)
                                }) {
                                    Icon(Icons.Default.Add, contentDescription = "Tambah")
                                }
                            }
                        }
                    }
                }
            }


            item {
                Spacer(Modifier.height(24.dp))

                val selectedItems = cartItems.filter { selectedIds.contains(it.id) }
                val subTotal = selectedItems.sumOf { it.price * it.quantity } // total harga asli
                val discountAmount = selectedItems.sumOf { (it.price * it.discount / 100) * it.quantity } // total potongan diskon
                val totalPayment = subTotal - discountAmount + adminFee + shippingFee // harga akhir yang dibayar

                Button(
                    onClick = {
                        Toast.makeText(context, "Checkout ${selectedIds.size} item", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Checkout (${selectedIds.size})")
                }

                Spacer(Modifier.height(24.dp))
                Text("Rincian Harga", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(8.dp))

                SummaryRow("Subtotal:", "Rp ${"%,.0f".format(subTotal)}", valueColor = Color.Gray)
                SummaryRow("Diskon Produk:", "- Rp ${"%,.0f".format(discountAmount)}", valueColor = Color.Green)
                SummaryRow("Biaya Admin:", "Rp ${"%,.0f".format(adminFee)}")
                SummaryRow("Ongkos Kirim:", "Rp ${"%,.0f".format(shippingFee)}")

                Divider(Modifier.padding(vertical = 8.dp))

                SummaryRow(
                    "Total Bayar:",
                    "Rp ${"%,.0f".format(totalPayment)}",
                    fontWeight = FontWeight.Bold,
                    valueColor = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

/// Helper composable untuk baris label–value
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
fun CartItemRow(
    item: CartItem,
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    onQuantityChange: (Int) -> Unit,
    onDelete: () -> Unit
) {
    var qty by remember { mutableStateOf(item.quantity) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            Checkbox(
                checked = selected,
                onCheckedChange = onSelectedChange
            )

            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.title,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("Rp ${"%,.0f".format(item.price)}", color = MaterialTheme.colorScheme.primary)

                Spacer(Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // minus
                    IconButton(
                        onClick = {
                            if (qty > 1) {
                                qty -= 1
                                onQuantityChange(qty)
                            }
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Kurang")
                    }

                    Text(qty.toString(), modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)

                    // plus
                    IconButton(
                        onClick = {
                            qty += 1
                            onQuantityChange(qty)
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Tambah")
                    }
                }
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Hapus")
            }
        }
    }
}
