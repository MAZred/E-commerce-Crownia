package com.ujikom.crownia.pages

data class FavoriteItem(
    val id: String = "",
    val title: String = "",
    val price: Double = 0.0,
    val actualPrice: Double = 0.0,
    val discount: Double = 0.0,
    val imageUrl: String = ""
)
