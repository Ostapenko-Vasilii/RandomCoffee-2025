package ru.vasiliiostapenko.randomcoffee.DataLayer.models

import com.google.gson.annotations.SerializedName


data class Category (

  @SerializedName("id"   ) var id   : Int?    = null,
  @SerializedName("slug" ) var slug : String? = null

)