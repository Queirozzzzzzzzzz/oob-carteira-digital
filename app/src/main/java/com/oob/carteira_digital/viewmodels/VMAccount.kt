package com.oob.carteira_digital.viewmodels

import androidx.lifecycle.ViewModel
import com.oob.carteira_digital.models.Account

class VMAccount : ViewModel() {
    private val account = Account()

    suspend fun login(cpf: String, password: String): String {
        if (cpf.isEmpty() || password.isEmpty()) {
            return "Complete os campos em branco."
        }

        return if (account.login(cpf, password)) {
            "Logado."
        } else {
            "incorrect"
        }
    }

    suspend fun checkLogin(): Boolean {
        return account.checkLogin()
    }

    suspend fun saveAccount() {
        val info = account.getAccountInfo()
    }
}