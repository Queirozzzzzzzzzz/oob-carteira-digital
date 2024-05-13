package com.oob.carteira_digital.models

import android.util.Log
import com.google.gson.Gson
import com.oob.carteira_digital.objects.Preferences
import com.oob.carteira_digital.api.Service
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

class Account {
    private val service = Service().getService()

    suspend fun login(cpf: String, password: String): String {
        if (cpf.isEmpty() || password.isEmpty()) {
            return "Preencha todos os campos!"
        }

        val params = HashMap<String?, String?>()
        params["cpf"] = cpf
        params["password"] = password
        val response = withContext(Dispatchers.IO) {
            service.login(params)
        }

        return if (response.isSuccessful) {
            Preferences.setAuthCookie(response.headers().values("Set-Cookie").toString())
            "true"
        } else {
            val gson = Gson()
            val result = gson.fromJson(response.errorBody()?.string(), Any::class.java)
            result.toString()
        }
    }

    suspend fun biometricLogin(): String {
        val cpf = "00000000000"
        val password = "a"
        return login(cpf, password)
    }

    fun checkLogin(): Boolean {
        var isExpired = true
        try {
            val currentDate = Date(System.currentTimeMillis())
            val expireDate = Date(Preferences.getAuthExpireDate().toLong())
            isExpired = currentDate > expireDate
        } catch (e: Exception) {
            Log.e("CHECK_LOGIN", e.toString())
        }

        return !isExpired
    }

    suspend fun getAccountInfo(): String {
        val response = withContext(Dispatchers.IO) {
            service.accountInfo()
        }

        return if (response.isSuccessful) {
            try {
                val result = response.body().toString()
                Log.d("GET_ACCOUNT_INFO", result)
                result
            } catch (e: Exception) {
                Log.e("GET_ACCOUNT_INFO", e.toString())
                ""
            }
        } else {
            ""
        }
    }
}