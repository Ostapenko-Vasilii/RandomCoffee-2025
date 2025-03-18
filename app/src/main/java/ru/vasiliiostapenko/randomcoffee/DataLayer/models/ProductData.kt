package ru.vasiliiostapenko.randomcoffee.DataLayer.models

import com.google.gson.annotations.SerializedName


data class ProductData (

  @SerializedName("id"          ) var id          : Int?              = null,
  @SerializedName("name"        ) var name        : String?           = null,
  @SerializedName("description" ) var description : String?           = null,
  @SerializedName("category"    ) var category    : Category?         = Category(),
  @SerializedName("imageUrl"    ) var imageUrl    : String?           = null,
  @SerializedName("prices"      ) var prices      : ArrayList<Prices> = arrayListOf()

)