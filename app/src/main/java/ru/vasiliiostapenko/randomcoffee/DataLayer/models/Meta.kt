package ru.vasiliiostapenko.randomcoffee.DataLayer.models

import com.google.gson.annotations.SerializedName


data class Meta (

  @SerializedName("page"  ) var page  : Int? = null,
  @SerializedName("size"  ) var size  : Int? = null,
  @SerializedName("count" ) var count : Int? = null

)