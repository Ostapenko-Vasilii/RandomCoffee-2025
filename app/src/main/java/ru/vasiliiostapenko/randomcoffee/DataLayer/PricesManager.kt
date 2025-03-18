package ru.vasiliiostapenko.randomcoffee.DataLayer

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ru.vasiliiostapenko.randomcoffee.DataLayer.ProductsDataManager.Companion.PRODUCTS_DATA
import ru.vasiliiostapenko.randomcoffee.DataLayer.models.ProductList

val Context.pricesDataBase: DataStore<Preferences> by preferencesDataStore(name = PricesManager.PRICES_DATA_STORAGE)

class PricesManager {
    suspend fun getRubPriceByIdDataStore(current: Context, productID: Int): Float {
        val PRICES_KEY = floatPreferencesKey(PRICES_DATA + productID.toString())
        val pref = current.pricesDataBase.data.map { preferences -> preferences[PRICES_KEY] ?: -1f }
            .first()
        return pref
    }

    suspend fun setActualRubPricesToDataStore(current: Context, map: Map<Int, Float>) {
        current.pricesDataBase.edit { preferences ->
            map.forEach { id, price ->
                val PRICES_KEY = floatPreferencesKey(PRICES_DATA + id.toString())
                preferences[PRICES_KEY] = price
            }
        }
    }

    companion object {
        const val PRICES_DATA = "piecesData"
        const val PRICES_DATA_STORAGE = "pricesDataStorage"
    }
}