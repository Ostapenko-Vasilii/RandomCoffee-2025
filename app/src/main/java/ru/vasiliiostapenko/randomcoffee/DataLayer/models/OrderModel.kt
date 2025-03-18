package ru.vasiliiostapenko.randomcoffee.DataLayer.models

import com.google.gson.annotations.SerializedName


data class OrderModel(
    @SerializedName("positions" ) var positions : Map<Int, Int>?,
    @SerializedName("token"     ) var token     : String

)



