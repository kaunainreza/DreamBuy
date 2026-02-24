package com.example.dreamdeal.ui.theme.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "cart_prefs")

class CartPreferences(private val context: Context) {
    private val gson = Gson()
    private val CART_ITEMS_KEY = stringPreferencesKey("cart_items")

    val cartItems: Flow<List<CartItem>> = context.dataStore.data
        .map { preferences ->
            val json = preferences[CART_ITEMS_KEY]
            if (json.isNullOrEmpty()) {
                emptyList()
            } else {
                val type = object : TypeToken<List<CartItem>>() {}.type
                gson.fromJson(json, type)
            }
        }

    suspend fun saveCartItems(items: List<CartItem>) {
        val json = gson.toJson(items)
        context.dataStore.edit { preferences ->
            preferences[CART_ITEMS_KEY] = json
        }
    }
}
