package com.oob.carteira_digital.models

import android.util.Log
import com.oob.carteira_digital.objects.Preferences
import com.oob.carteira_digital.api.Service
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Account {
    private val service = Service().getService()

    suspend fun login(cpf: String, password: String): Boolean {
        val params = HashMap<String?, String?>()
        params["cpf"] = cpf
        params["password"] = password

        val response = withContext(Dispatchers.IO) {
            service.login(params)
        }

        if (response.isSuccessful) {
            Preferences.setAuthCookie(response.headers().values("Set-Cookie").toString())
            return true
        }

        return false
    }

    suspend fun checkLogin(): Boolean {
        val response = withContext(Dispatchers.IO) {
            service.accountInfo()
        }

        return response.isSuccessful
    }

    suspend fun getAccountInfo(): String {
        val response = withContext(Dispatchers.IO) {
            service.accountInfo()
        }

        return if (response.isSuccessful) {
            try {
                response.body().toString()
            } catch (e: Exception) {
                Log.e("GET_ACCOUNT_INFO", e.toString())
                ""
            }
        } else {
            ""
        }
    }
}