package com.example.androidnavigatorleti.preferences

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)

    fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun getString(key: String, def: String?): String {
        return prefs.getString(key, def) ?: ""
    }

    fun putBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, def: Boolean): Boolean {
        return prefs.getBoolean(key, def)
    }

    fun putInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    fun getInt(key: String, def: Int): Int {
        return prefs.getInt(key, def)
    }

    fun putLong(key: String, value: Long) {
        prefs.edit().putLong(key, value).apply()
    }

    fun getLong(key: String, def: Long): Long {
        return prefs.getLong(key, def)
    }

    fun putDouble(key: String, value: Double) {
        putLong(key, java.lang.Double.doubleToRawLongBits(value))
    }

    fun getDouble(key: String, def: Double): Double {
        return java.lang.Double.longBitsToDouble(
            getLong(
                key,
                java.lang.Double.doubleToRawLongBits(def)
            )
        )
    }

    object Keys {

        const val LAT_KEY = "LAT_KEY"
        const val LNG_KEY = "LNG_KEY"
        const val HISTORY_ENABLED = "history_enabled"
    }
}