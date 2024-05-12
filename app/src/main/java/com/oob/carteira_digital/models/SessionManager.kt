package com.oob.carteira_digital.models

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.oob.carteira_digital.LoginActivity
import com.oob.carteira_digital.MainActivity2
import com.oob.carteira_digital.viewmodels.VMAccount
import com.oob.carteira_digital.objects.Preferences

class SessionManager {
    private var pref: SharedPreferences
    private var editor: SharedPreferences.Editor
    private var con: Context
    private var PRIVATE_MODE: Int = 0
    private var viewModel: VMAccount

    constructor(viewModelStoreOwner: ViewModelStoreOwner, con: Context) {
        this.con = con
        pref = con.getSharedPreferences(Preferences.PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
        viewModel = ViewModelProvider(viewModelStoreOwner)[VMAccount::class.java]
    }

    fun createSession(cookie: String) {
        editor.putString(Preferences.KEY_AUTH_COOKIE, cookie)
        editor.commit()

        val i = Intent(con, MainActivity2::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        con.startActivity(i)
    }

    suspend fun isLoggedIn(): Boolean {
        return viewModel.checkLogin()
    }

    fun endSession() {
        editor.clear()
        editor.commit()

        val i = Intent(con, LoginActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        con.startActivity(i)
    }

}
