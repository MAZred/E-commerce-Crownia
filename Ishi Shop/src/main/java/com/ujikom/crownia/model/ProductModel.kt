package com.ujikom.crownia.model

data class ProductModel(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val discount: Double = 0.0,
    val price: Double = 0.0,        // Harga asli (misalnya harga before diskon)
    val actualPrice: Double = 0.0,  // Harga yang dijual (setelah diskon)
    val category: String = "",
    val images: List<String> = emptyList()
)


