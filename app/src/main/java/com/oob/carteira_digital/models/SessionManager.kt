package com.oob.carteira_digital.models

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.oob.carteira_digital.BaseActivity
import com.oob.carteira_digital.LoginActivity
import com.oob.carteira_digital.viewmodels.VMAccount
import com.oob.carteira_digital.objects.Preferences

class

SessionManager {
    private var con: Context
    private var viewModel: VMAccount

    constructor(viewModelStoreOwner: ViewModelStoreOwner, con: Context) {
        this.con = con
        viewModel = ViewModelProvider(viewModelStoreOwner)[VMAccount::class.java]
    }


    fun startSession() {
        Preferences.setStartup(true)
        val i = Intent(con, BaseActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        con.startActivity(i)
    }

    fun isLoggedIn(): Boolean {
        return viewModel.checkLogin()
    }

    fun endSession() {
        Preferences.setAuthExpireDate("")
        Preferences.setStartup(true)
        val i = Intent(con, LoginActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        con.startActivity(i)
    }

    fun setTheme() {
        val isChecked = Preferences.isDarkTheme()
        if (isChecked) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

}
