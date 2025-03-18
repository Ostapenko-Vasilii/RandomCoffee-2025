package ru.vasiliiostapenko.randomcoffee.DataLayer

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ru.vasiliiostapenko.randomcoffee.DataLayer.models.ProductData
import ru.vasiliiostapenko.randomcoffee.DataLayer.models.ProductList

val Context.productsDataBase: DataStore<Preferences> by preferencesDataStore(name = ProductsDataManager.PRODUCTS_STORAGE_NAME)

class ProductsDataManager() {

    suspend fun getProductsDataStore(current: Context, pageId: Int): ProductList? {
        val PRODUCT_LIST_KEY = stringPreferencesKey(PRODUCTS_DATA + pageId.toString())
        val pref =
            current.productsDataBase.data.map { preferences -> preferences[PRODUCT_LIST_KEY] ?: "" }
                .first()

        try {
            val productsObject = jsonDeserProductList(pref)
            Log.d("getProductsDataStore", productsObject.toString())
            return productsObject
        } catch (e: Exception) {
            Log.e("getProductsDataStore", e.fillInStackTrace().message.toString())
            return null
        }

    }

    suspend fun setProductsDataStore(current: Context, productDataListJson: String, pageId: Int) {
        val PRODUCT_LIST_KEY = stringPreferencesKey(PRODUCTS_DATA + pageId.toString())
        current.productsDataBase.edit { preferences ->
            preferences[PRODUCT_LIST_KEY] = productDataListJson
        }
        Log.d("setProductsDataStore", "done")
    }

    companion object {
        const val PRODUCTS_STORAGE_NAME = "products_storage"
        const val PRODUCTS_DATA = "products_data"
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
}