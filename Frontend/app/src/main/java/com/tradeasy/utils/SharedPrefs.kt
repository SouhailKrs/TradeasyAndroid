package com.tradeasy.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.tradeasy.domain.model.User

@Suppress("UNCHECKED_CAST")
class SharedPrefs(private val context: Context) {

    companion object {
        private const val PREF = "TRADEASY_PREFS"
        private const val PREF_USER = "TRADEASY_USER"
        private const val PREF_TOKEN = "USER_TOKEN"
    }

    private val sharedPref: SharedPreferences =
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
    private val gson = Gson()

    fun setUser(user: User) {
        sharedPref.edit().putString(PREF_USER, gson.toJson(user)).apply()
    }

    fun getUser(): User? {
        return gson.fromJson(sharedPref.getString(PREF_USER, null), User::class.java)
    }

    fun getToken(): String? {
        return sharedPref.getString(PREF_TOKEN, null)
    }

    fun setToken(token: String) {
        sharedPref.edit().putString(PREF_TOKEN, token).apply()
    }

    private fun <T> get(key: String, clazz: Class<T>): T = when (clazz) {
        String::class.java -> sharedPref.getString(key, "")
        Boolean::class.java -> sharedPref.getBoolean(key, false)
        Float::class.java -> sharedPref.getFloat(key, -1f)
        Double::class.java -> sharedPref.getFloat(key, -1f)
        Int::class.java -> sharedPref.getInt(key, -1)
        Long::class.java -> sharedPref.getLong(key, -1L)
        else -> null
    } as T

    private fun <T> put(key: String, data: T) {
        val editor = sharedPref.edit()
        when (data) {
            is String -> editor.putString(key, data)
            is Boolean -> editor.putBoolean(key, data)
            is Float -> editor.putFloat(key, data)
            is Double -> editor.putFloat(key, data.toFloat())
            is Int -> editor.putInt(key, data)
            is Long -> editor.putLong(key, data)
        }
        editor.apply()
    }

    fun clear() {
        sharedPref.edit().run {
            remove(PREF_USER)
        }.apply()
    }


}