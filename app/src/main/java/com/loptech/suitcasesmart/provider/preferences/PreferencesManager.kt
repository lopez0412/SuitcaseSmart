package com.loptech.suitcasesmart.provider.preferences

import android.content.Context
import android.content.SharedPreferences
import com.loptech.suitcasesmart.R

class PreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(R.string.preferences.toString(), Context.MODE_PRIVATE)

    fun saveData(key: String, value: String){
        val editor = sharedPreferences.edit()
        editor.putString(key,value)
        editor.apply()
    }

    fun saveData(key: String, value: Boolean){
        val editor = sharedPreferences.edit()
        editor.putBoolean(key,value)
        editor.apply()
    }

    fun getData(key: String, defaultValue:String): String{
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    fun getData(key: String, defaultValue: Boolean):Boolean{
        return sharedPreferences.getBoolean(key, defaultValue)
    }
}