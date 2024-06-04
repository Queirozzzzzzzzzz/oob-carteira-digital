package com.oob.carteira_digital.models

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.oob.carteira_digital.CardFragment
import com.oob.carteira_digital.api.Service
import com.oob.carteira_digital.objects.Preferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date


class Account {
    private val service = Service().getService()
    private val gson = Gson()

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
            Preferences.setAuthCpf(cpf)
            Preferences.setAuthPassword(password)
            "true"
        } else {
            try {
                response.errorBody()?.string().toString()
            } catch (e: Exception) {
                Log.e("LOGIN", e.toString())
                "Falha interna."
            }
        }
    }

    suspend fun biometricLogin(): String {
        if (Preferences.getAuthCpf().isEmpty() || Preferences.getAuthPassword().isEmpty()) {
            return "Login biométrico sem dados salvos. Preencha o formulário manualmente."
        }
        return login(Preferences.getAuthCpf(), Preferences.getAuthPassword())
    }

    fun offlineLogin(cpf: String, password: String): String {
        return if (cpf == Preferences.getAuthCpf() && password == Preferences.getAuthPassword()) {
            val expireDate = (System.currentTimeMillis() + 3600000).toString()
            Preferences.setAuthExpireDate(expireDate)
            "true"
        } else {
            "Login inválido."
        }
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

    suspend fun getAccountInfo(): List<String> {
        val response = withContext(Dispatchers.IO) {
            service.accountInfo()
        }

        return if (response.isSuccessful) {
            try {
                val result = gson.fromJson(response.body()?.string(), Any::class.java)
                var stringList = (result as Map<*, *>).values.map { it.toString() }
                stringList = stringList.map {
                    if (it.endsWith(".0")) {
                        it.substring(0, it.length - 2)
                    } else {
                        it
                    }
                }
                stringList
            } catch (e: Exception) {
                Log.e("GET_ACCOUNT_INFO", e.toString())
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    fun getCourses(coursesRaw: String): List<CardFragment.Course> {
        val courses = coursesRaw.replace("class", "c_class")
        val nCourses: List<CardFragment.Course> = Gson().fromJson(
            courses, object : TypeToken<List<CardFragment.Course?>?>() {}.type
        )

        return nCourses
    }

    suspend fun forgotPassword(email: String): String {
        val response = withContext(Dispatchers.IO) {
            service.forgotPassword(email)
        }

        return if (!response.isSuccessful) {
            val result = gson.fromJson(response.errorBody()?.string(), Any::class.java)
            Log.e("RESET_PASSWORD", result.toString())
            "false"
        } else {
            "true"
        }
    }

    suspend fun resetPassword(newPassword: String, confirmNewPassword: String): String {
        val params = HashMap<String?, String?>()
        params["new_password"] = newPassword
        params["confirm_new_password"] = confirmNewPassword

        val response = withContext(Dispatchers.IO) {
            service.resetPassword(params)
        }

        return if (!response.isSuccessful) {
            val result = gson.fromJson(response.errorBody()?.string(), Any::class.java)
            Log.e("RESET_PASSWORD", result.toString())
            "Falha interna."
        } else {
            if (response.code() == 200) {
                "true"
            } else {
                gson.fromJson(response.errorBody()?.string(), Any::class.java).toString()
            }
        }
    }
}