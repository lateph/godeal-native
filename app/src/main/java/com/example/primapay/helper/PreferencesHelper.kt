package com.example.primapay.helper

import android.content.Context
import android.preference.PreferenceManager

class PreferencesHelper(context: Context){
    companion object {
        val DEVELOP_MODE = false
        private const val ACCESS_TOKEN = "data.source.prefs.DEVICE_TOKEN"
        private const val REFRESH_TOKEN = "data.source.prefs.REFRESH_TOKEN"
    }
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    // save device token
    var accessToken = preferences.getString(ACCESS_TOKEN, "")
        set(value) = preferences.edit().putString(ACCESS_TOKEN,     value).apply()

    var refreshToken = preferences.getString(REFRESH_TOKEN, "")
        set(value) = preferences.edit().putString(REFRESH_TOKEN,     value).apply()

    open fun logout(){
        accessToken = ""
        refreshToken = ""
    }
}