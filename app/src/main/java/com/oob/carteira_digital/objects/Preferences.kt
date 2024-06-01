package com.oob.carteira_digital.objects

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.core.content.edit

object Preferences {
    const val PREF_NAME: String = "carteiraDigitalPreferences"

    private var prefs: SharedPreferences? = null

    fun setup(con: Context) {
        prefs = con.getSharedPreferences(PREF_NAME, MODE_PRIVATE)
    }

    private fun extractToken(tokenString: String): String {
        val regex = Regex("""token=([^;]+)""")
        val matchResult = regex.find(tokenString)
        val token = matchResult?.groups?.get(1)?.value ?: ""
        return if (token.isNotEmpty()) "token=$token" else ""
    }

    private fun extractTokenExpireDate(tokenString: String): String {
        val regex = Regex("""Max-Age=(\d+)""")
        val matchResult = regex.find(tokenString)
        val maxAge = matchResult?.groups?.get(1)?.value ?: ""
        val maxAgeInt = maxAge.toIntOrNull() ?: return ""

        val currentTimeMillis = System.currentTimeMillis()
        val expireDate = currentTimeMillis + (maxAgeInt * 1000)

        return expireDate.toString()
    }

    private fun setAuthCookieExpireDate(token: String) {
        val expireDate = extractTokenExpireDate(token)
        Key.KEY_AUTH_EXPIRE_DATE.setString(expireDate)
    }

    fun setAuthExpireDate(expireDate: String) {
        Key.KEY_AUTH_EXPIRE_DATE.setString(expireDate)
    }

    fun setAuthCookie(token: String) {
        setAuthCookieExpireDate(token)
        val extractedToken = extractToken(token)
        Key.KEY_AUTH_TOKEN.setString(extractedToken)
    }

    fun getAuthCookie(): String? {
        return Key.KEY_AUTH_TOKEN.getString()
    }

    fun getAuthExpireDate(): String {
        return Key.KEY_AUTH_EXPIRE_DATE.getString().toString()
    }

    fun setAccountId(id: String) {
        Key.KEY_ACCOUNT_ID.setString(id)
    }

    fun getAccountId(): String {
        return Key.KEY_ACCOUNT_ID.getString().toString()
    }

    fun setAuthCpf(cpf: String) {
        Key.KEY_AUTH_CPF.setString(cpf)
    }

    fun getAuthCpf(): String {
        return Key.KEY_AUTH_CPF.getString().toString()
    }

    fun setAuthPassword(password: String) {
        Key.KEY_AUTH_PASSWORD.setString(password)
    }

    fun getAuthPassword(): String {
        return Key.KEY_AUTH_PASSWORD.getString().toString()
    }

    fun setDarkTheme(darkTheme: Boolean) {
        Key.KEY_DARK_THEME.setBoolean(darkTheme)
    }

    fun isDarkTheme(): Boolean {
        return Key.KEY_DARK_THEME.getBoolean() ?: false
    }

    fun isStartup(): Boolean {
        return Key.KEY_STARTUP.getBoolean() ?: false
    }

    fun setStartup(startup: Boolean) {
        Key.KEY_STARTUP.setBoolean(startup)
    }

    private enum class Key {
        KEY_AUTH_TOKEN, KEY_AUTH_EXPIRE_DATE, KEY_ACCOUNT_ID, KEY_AUTH_CPF, KEY_AUTH_PASSWORD, KEY_DARK_THEME, KEY_STARTUP;

        fun getBoolean(): Boolean? =
            if (prefs!!.contains(name)) prefs!!.getBoolean(name, false) else null

        fun getFloat(): Float? = if (prefs!!.contains(name)) prefs!!.getFloat(name, 0f) else null
        fun getInt(): Int? = if (prefs!!.contains(name)) prefs!!.getInt(name, 0) else null
        fun getLong(): Long? = if (prefs!!.contains(name)) prefs!!.getLong(name, 0) else null
        fun getString(): String? = if (prefs!!.contains(name)) prefs!!.getString(name, "") else null

        fun setBoolean(value: Boolean?) =
            value?.let { prefs!!.edit { putBoolean(name, value) } } ?: remove()

        fun setFloat(value: Float?) =
            value?.let { prefs!!.edit { putFloat(name, value) } } ?: remove()

        fun setInt(value: Int?) = value?.let { prefs!!.edit { putInt(name, value) } } ?: remove()
        fun setLong(value: Long?) = value?.let { prefs!!.edit { putLong(name, value) } } ?: remove()
        fun setString(value: String?) =
            value?.let { prefs!!.edit { putString(name, value) } } ?: remove()

        fun exists(): Boolean = prefs!!.contains(name)
        fun remove() = prefs!!.edit { remove(name) }
    }
}