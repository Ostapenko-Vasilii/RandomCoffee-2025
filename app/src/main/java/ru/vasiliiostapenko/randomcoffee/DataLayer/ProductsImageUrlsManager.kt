package ru.vasiliiostapenko.randomcoffee.DataLayer

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ru.vasiliiostapenko.randomcoffee.DataLayer.ProductsDataManager.Companion.PRODUCTS_DATA
import ru.vasiliiostapenko.randomcoffee.DataLayer.ProductsImageUrlsManager.Companion.PRODUCTS_URL_DATA_STORAGE

val Context.productImageUrlsDataBase: DataStore<Preferences> by preferencesDataStore(name = PRODUCTS_URL_DATA_STORAGE)

class ProductsImageUrlsManager() {
    suspend fun getUrlByIdDataStore(current: Context, productID: Int): String {
        val URL_KEY = stringPreferencesKey(PRODUCT_URL_DATA + productID.toString())
        val pref =
            current.productImageUrlsDataBase.data.map { preferences -> preferences[URL_KEY] ?: "" }
                .first()
        return pref
    }

    suspend fun setActualImageUrlToDataStore(current: Context, map: Map<Int, String>) {
        current.productImageUrlsDataBase.edit { preferences ->
            map.forEach { id, price ->
                val URL_KEY = stringPreferencesKey(PRODUCT_URL_DATA + id.toString())
                preferences[URL_KEY] = price
            }
        }
    }

    companion object {
        const val PRODUCT_URL_DATA = "productURLData"
        const val PRODUCTS_URL_DATA_STORAGE = "productURLDataStorage"
    }

}