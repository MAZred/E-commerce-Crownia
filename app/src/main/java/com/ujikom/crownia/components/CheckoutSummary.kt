package com.ujikom.crownia.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CheckoutSummaryCard(
    totalPrice: Double,
    totalDiscountedPrice: Double,
    modifier: Modifier = Modifier,
    onCheckoutClick: () -> Unit
) {
    val adminFee = 2000.0
    val shippingFee = 15000.0
    val shippingDiscount = 5000.0

    val totalPayment = totalDiscountedPrice + adminFee + shippingFee - shippingDiscount

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Ringkasan Pembayaran",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            SummaryRow("Total Harga Barang:", totalPrice)
            SummaryRow("Diskon Produk:", totalPrice - totalDiscountedPrice, isNegative = true)
            SummaryRow("Biaya Admin:", adminFee)
            SummaryRow("Biaya Pengiriman:", shippingFee)
            SummaryRow("Diskon Ongkir:", shippingDiscount, isNegative = true)

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 1.dp,
                color = Color.Gray
            )

            SummaryRow(
                label = "Total Bayar:",
                value = totalPayment,
                isBold = true,
                valueColor = Color(0xFF00FF88)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onCheckoutClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Checkout Sekarang")
            }
        }
    }
}

@Composable
fun SummaryRow(
    label: String,
    value: Double,
    isNegative: Boolean = false,
    isBold: Boolean = false,
    valueColor: Color = Color.White
) {
    val displayValue = if (isNegative) "- Rp ${value.toInt()}" else "Rp ${value.toInt()}"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = if (isBold) MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            else MaterialTheme.typography.bodyMedium,
            color = Color.White
        )
        Text(
            text = displayValue,
            style = if (isBold) MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            else MaterialTheme.typography.bodyMedium,
            color = valueColor
        )
    }
}
