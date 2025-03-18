package ru.vasiliiostapenko.randomcoffee.DataLayer.models

import com.google.gson.annotations.SerializedName


data class  ProductList (

  @SerializedName("data" ) var data : ArrayList<ProductData> = arrayListOf(),
  @SerializedName("meta" ) var meta : Meta?           = Meta()

)