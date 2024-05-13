package com.oob.carteira_digital.viewmodels

import androidx.lifecycle.ViewModel
import com.oob.carteira_digital.models.Account

class VMAccount : ViewModel() {
    private val account = Account()

    suspend fun login(cpf: String, password: String): String {
        return account.login(cpf, password)
    }

    suspend fun biometricLogin(): String {
        return account.biometricLogin()
    }

    fun checkLogin(): Boolean {
        return account.checkLogin()
    }

    suspend fun saveAccount() {
        val info = account.getAccountInfo()
    }
}