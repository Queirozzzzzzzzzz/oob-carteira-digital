package com.oob.carteira_digital

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.oob.carteira_digital.models.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class BaseActivity : AppCompatActivity() {

    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        session = SessionManager(this, applicationContext)
        checkLogin()
    }

    override fun onResume() {
        super.onResume()
        checkLogin()
    }

    private fun checkLogin() {
        CoroutineScope(Dispatchers.IO).launch {
            if (!session.isLoggedIn()) {
                session.endSession()
            }
        }
    }
}