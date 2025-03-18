package ru.vasiliiostapenko.randomcoffee.data

import ru.vasiliiostapenko.randomcoffee.DataLayer.models.Category
import ru.vasiliiostapenko.randomcoffee.DataLayer.models.ProductData

data class CategoryWithProducts(
    val category : Category,
    val products: ArrayList<ProductData> = arrayListOf()
)