package com.oob.carteira_digital.viewmodels

import androidx.lifecycle.ViewModel
import com.oob.carteira_digital.models.Account

class VMAccount : ViewModel() {
    private val account = Account()

    suspend fun login(cpf: String, password: String): String {
        return account.login(cpf, password)
    }

    fun checkLogin(): Boolean {
        return account.checkLogin()
    }

    suspend fun getInfo(): List<String> {
        val info  = account.getAccountInfo()
        return info
    }
}