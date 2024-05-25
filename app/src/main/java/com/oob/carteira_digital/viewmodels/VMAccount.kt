package com.oob.carteira_digital.viewmodels

import androidx.lifecycle.ViewModel
import com.oob.carteira_digital.models.Account
import com.oob.carteira_digital.objects.Preferences

class VMAccount : ViewModel() {
    private val account = Account()

    suspend fun login(cpf: String, password: String): String {
        return account.login(cpf, password)
    }

    fun offlineLogin(cpf: String, password: String): String {
        return account.offlineLogin(cpf, password)
    }

    fun offlineBiometricLogin(): String {
        return account.offlineLogin(Preferences.getAuthCpf(), Preferences.getAuthPassword())
    }

    suspend fun biometricLogin(): String {
        return account.biometricLogin()
    }

    fun checkLogin(): Boolean {
        return account.checkLogin()
    }

    suspend fun getInfo(): List<String> {
        val info = account.getAccountInfo()
        return info
    }
}