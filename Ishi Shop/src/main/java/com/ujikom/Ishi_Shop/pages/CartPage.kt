package com.ujikom.Ishi_Shop.pages

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ujikom.Ishi_Shop.components.TitleTopBar
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme.colorScheme
import com.ujikom.Ishi_Shop.components.CartItemCard
import com.ujikom.Ishi_Shop.components.SummaryRow


@Composable
fun CartPage(navController: NavController, modifier: Modifier = Modifier) {

    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val cartItems = remember { mutableStateListOf<CartItem>() }
    val selectedIds = remember { mutableStateListOf<String>() }
    val context = LocalContext.current
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    var adminFee by remember { mutableStateOf(2000.0) }
    var shippingFee by remember { mutableStateOf(15000.0) }
    var showSummary by remember { mutableStateOf(true) }

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
    )

    { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Daftar Item
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding() - 90.dp,bottom = 140.dp, start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cartItems) { item ->
                    CartItemCard(
                        item = item,
                        isChecked = selectedIds.contains(item.id),
                        onCheckedChange = { checked ->
                            if (checked) selectedIds += item.id else selectedIds -= item.id
                        }
                    )
                }
            }

            val selectedItems = cartItems.filter { selectedIds.contains(it.id) }
            val subTotal = selectedItems.sumOf { it.price * it.quantity }
            val discountAmount = selectedItems.sumOf { (it.price * it.discount / 100) * it.quantity }
            val totalPayment = subTotal - discountAmount + adminFee + shippingFee

            // Tombol Kembali Tampilkan Ringkasan
            if (selectedIds.isNotEmpty() && !showSummary) {
                Button(
                    onClick = { showSummary = true },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottomPadding + 50.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.secondary)
                ) {
                    Text("Lihat Rincian")
                }
            }

            // Popup Ringkasan Harga
            AnimatedVisibility(
                visible = selectedIds.isNotEmpty() && showSummary,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottomPadding + 45.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                    color = colorScheme.surface,
                    tonalElevation = 8.dp,
                    shadowElevation = 12.dp
                ) {
                    Column(modifier = Modifier.padding(2.dp)) {
                        // Tombol "Tutup Ringkasan"
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            IconButton(onClick = { showSummary = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Tutup ringkasan")
                            }
                        }

                        SummaryRow("Subtotal = ", "Rp ${"%,.0f".format(subTotal)}", valueColor = Color.Gray)
                        SummaryRow("Diskon = ", "- Rp ${"%,.0f".format(discountAmount)}", valueColor = colorScheme.primary)
                        SummaryRow("Admin = ", "Rp ${"%,.0f".format(adminFee)}")
                        SummaryRow("Ongkir = ", "Rp ${"%,.0f".format(shippingFee)}")

                        HorizontalDivider(Modifier.padding(vertical = 10.dp))

                        SummaryRow(
                            "Total",
                            "Rp ${"%,.0f".format(totalPayment)}",
                            fontWeight = FontWeight.Bold,
                            valueColor = colorScheme.primary
                        )

                        Spacer(Modifier.height(14.dp))

                        Button(
                            onClick = {
                                Toast.makeText(context, "Checkout ${selectedIds.size} item", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Checkout (${selectedIds.size})", fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}

/// Helper composable untuk baris label–value