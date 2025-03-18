package ru.vasiliiostapenko.randomcoffee.DataLayer.models

import com.google.gson.annotations.SerializedName

data class CategoriesList (
    @SerializedName("categories" ) var categories : ArrayList<Category> = arrayListOf(),
    @SerializedName("meta"       ) var meta       : Meta?                 = Meta()
)