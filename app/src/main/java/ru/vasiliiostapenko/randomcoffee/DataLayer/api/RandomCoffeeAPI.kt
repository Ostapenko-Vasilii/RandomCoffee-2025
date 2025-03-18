package ru.vasiliiostapenko.randomcoffee.DataLayer.api

import android.util.Log
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import ru.vasiliiostapenko.randomcoffee.DataLayer.models.ProductList
import ru.vasiliiostapenko.randomcoffee.DataLayer.models.CategoriesList
import ru.vasiliiostapenko.randomcoffee.DataLayer.models.OrderModel
import ru.vasiliiostapenko.randomcoffee.DataLayer.models.SuccessfulOrderResponseModel

class RandomCoffeeAPI() {
    private fun initClient(): OkHttpClient {
        val client = OkHttpClient()
        return client
    }

    fun getProductsByPage(
        pageID: Int,
        limit: Int,
        saveProductListToStorage: (requestBody: String, pageID: Int) -> Unit,
        onError: () -> Unit
    ): ProductList? {
        var lim = limit
        if (lim > 100) {
            lim = 100
        } else if (lim < 0) {
            lim = 0
        }
        val client = initClient()
        val productsRequest =
            Request.Builder().url("$BASE_URL$API_VER$PRODUCTS_API?$PAGE_API$pageID&$LIMIT_API$lim")
                .build()
        val response = sendRequest(client, productsRequest)
        if (response != null) {
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                Log.d("res", responseBody.toString())
                saveProductListToStorage(responseBody.toString(), pageID)
                val productListObj = jsonDeserProductList(responseBody.toString())
                return productListObj
            } else {
                Log.d("stop", response.message + response.code + response.body)
                onError()
                return null
            }
        } else {
            onError()
            return null
        }
    }

    fun createOrderRequest(orderModel: OrderModel): Boolean {
        val client = initClient()
        try {
            val responseOrder =
                Gson().toJson(orderModel).toRequestBody("application/json".toMediaType())
            val request =
                Request.Builder().url("$BASE_URL$API_VER$ORDER_API")
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .post(responseOrder)
                    .build()
            val response = sendRequest(client, request)
            Log.d("res", response?.code.toString())
            if (response?.isSuccessful == true) {
                val responseBody = response?.body?.string()
                val responseOrderModel =
                    Gson().fromJson(responseBody, SuccessfulOrderResponseModel::class.java)

                if (SUCCESSFUL_ORDER.equals(responseOrderModel.message.toString())) {
                    return true
                } else {
                    return false
                }
            } else {
                return false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

    private fun sendRequest(client: OkHttpClient, Request: Request): Response? {
        try {
            print("send")
            val response = client.newCall(Request).execute()
            print("get")
            return response
        } catch (e: Exception) {

            e.printStackTrace()
            return null
        }
    }


    private fun jsonDeserProductList(body: String): ProductList? {
        try {
            var productsObject = Gson().fromJson(body, ProductList::class.java)
            return productsObject
        } catch (e: Exception) {
            Log.e("Deser Error", e.fillInStackTrace().message.toString() + body)
            return null
        }
    }

    private fun jsonDeserCategoryList(body: String): CategoriesList? {
        try {
            var productsObject = Gson().fromJson(body, CategoriesList::class.java)
            return productsObject
        } catch (e: Exception) {
            Log.e("Deser Error", e.fillInStackTrace().message.toString() + body)
            return null
        }
    }

    companion object {
        const val BASE_URL = "https://coffeeshop.academy.effective.band/api/"
        const val API_VER = "v1/"
        const val PRODUCTS_API = "products/"
        const val PAGE_API = "page="
        const val LIMIT_API = "limit="
        const val ORDER_API = "orders"
        const val SUCCESSFUL_ORDER = "success"
    }
}