package ru.vasiliiostapenko.randomcoffee.DataLayer.models

import com.google.gson.annotations.SerializedName

data class SuccessfulOrderResponseModel(
    @SerializedName("message") var message: String? = null,
    @SerializedName("orderId") var orderId: String? = null,
)

data class ErrorOrderResponseModel(
    @SerializedName("detail") var detail: String? = null,
)