package ru.vasiliiostapenko.randomcoffee.DataLayer.models

data class ProductInCardModel (
    val productId: Int,
    val productName: String,
    val count: Int,
    val price: Float,
    val imageUrl: String
)