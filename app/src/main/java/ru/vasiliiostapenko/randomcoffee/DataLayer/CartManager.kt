package ru.vasiliiostapenko.randomcoffee.DataLayer

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first


val Context.cartDataBase: DataStore<Preferences> by preferencesDataStore(name = CartManager.CART_STORAGE_NAME)

class CartManager() {
    suspend fun getCartFromData(current: Context): MutableMap<Int, Int> {
        val pref = current.cartDataBase.data.first()
        var data: MutableMap<Int, Int> = mutableMapOf()
        for (product in pref.asMap()) {
            data.put(product.key.name.toString().toInt(), product.value.toString().toInt())
        }
        return data
    }

    suspend fun addOneProductToCart(current: Context, productId: Int) {
        val PRODUCT_KEY = intPreferencesKey(productId.toString())

        current.cartDataBase.edit { products ->
            val currentValue = products[PRODUCT_KEY] ?: 0
            if (currentValue < 10) {
                products[PRODUCT_KEY] = currentValue + 1
            }
        }
    }

    suspend fun removeOneProductFromCart(current: Context, productId: Int) {
        val PRODUCT_KEY = intPreferencesKey(productId.toString())

        current.cartDataBase.edit { products ->
            val currentValue = products[PRODUCT_KEY] ?: 0
            if (currentValue <= 1) {
                products.remove(PRODUCT_KEY)
            } else {
                products[PRODUCT_KEY] = currentValue - 1
            }
        }
    }

    suspend fun removeAllProductsFromCart(current: Context) {
        current.cartDataBase.edit { products -> products.clear() }
    }

    companion object {
        const val CART_STORAGE_NAME = "CART_STORAGE"

    }
}
