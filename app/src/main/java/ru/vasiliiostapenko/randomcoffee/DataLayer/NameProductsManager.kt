package ru.vasiliiostapenko.randomcoffee.DataLayer

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ru.vasiliiostapenko.randomcoffee.DataLayer.NameProductsManager.Companion.PRODUCTS_NAMES_DATA_STORAGE
import ru.vasiliiostapenko.randomcoffee.DataLayer.ProductsDataManager.Companion.PRODUCTS_DATA

val Context.namesDataBase: DataStore<Preferences> by preferencesDataStore(name = PRODUCTS_NAMES_DATA_STORAGE)

class NameProductsManager() {
    suspend fun getNamesByIdDataStore(current: Context, productID: Int): String {
        val NAMES_KEY = stringPreferencesKey(PRODUCT_NAMES_DATA + productID.toString())
        val pref =
            current.namesDataBase.data.map { preferences -> preferences[NAMES_KEY] }.first() ?: ""
        return pref
    }

    suspend fun setActualNamesToDataStore(current: Context, map: Map<Int, String>) {
        current.namesDataBase.edit { preferences ->
            map.forEach { id, price ->
                val NAME_KEY = stringPreferencesKey(PRODUCT_NAMES_DATA + id.toString())
                preferences[NAME_KEY] = price
            }
        }
    }

    companion object {
        const val PRODUCT_NAMES_DATA = "productNamesData"
        const val PRODUCTS_NAMES_DATA_STORAGE = "productNamesDataStorage"
    }

}