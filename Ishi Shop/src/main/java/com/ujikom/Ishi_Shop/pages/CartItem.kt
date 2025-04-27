package com.ujikom.Ishi_Shop.pages


data class CartItem(
    var id: String = "",
    var title: String = "",
    var price: Double = 0.0,
    var actualPrice: Double = 0.0,
    var discount: Double = 0.0,
    var quantity: Int = 0,
    var imageUrl: String = ""
)



