package ru.vasiliiostapenko.randomcoffee.DataLayer.models

import com.google.gson.annotations.SerializedName


data class Prices (

  @SerializedName("value"    ) var value    : String? = null,
  @SerializedName("currency" ) var currency : String? = null

)