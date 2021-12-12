package com.example.tokobuku.util

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.example.tokobuku.core.data.model.Costumer
import com.google.gson.Gson

class SharedPref(activity: Activity) {
    var login = "Login"
    var nama = "nama"
    var email = "email"
    var phone = "phone"
    var costumer = "costumer"
    val mypref = "MY_PREF"
    val sp: SharedPreferences

    init {
        sp = activity.getSharedPreferences(mypref, Context.MODE_PRIVATE)
    }

    fun setStatusLogin(status: Boolean) {
        sp.edit().putBoolean(login, status).apply()
    }

    fun getStatusLogin(): Boolean {
        return sp.getBoolean(login, false)
    }

    fun setUser(value: Costumer) {
        val data: String = Gson().toJson(value, Costumer::class.java)
        sp.edit().putString(costumer, data).apply()
    }

    fun getUser(): Costumer? {
        val data: String = sp.getString(costumer, null) ?: return null
        return Gson().fromJson<Costumer>(data, Costumer::class.java)
    }

    fun setString(key: String, value: String) {
        sp.edit().putString(key, value).apply()
    }

    fun getStrin(key: String): String {
        return sp.getString(key, "")!!
    }
}