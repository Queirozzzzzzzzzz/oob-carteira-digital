package com.oob.carteira_digital

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.oob.carteira_digital.models.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity2 : AppCompatActivity() {
    private lateinit var session: SessionManager
    private lateinit var logoutBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        checkLogin()

        logoutBtn = findViewById(R.id.logout)
        logoutBtn.setOnClickListener {
            logout()
        }
    }

    private fun checkLogin() {
        session = SessionManager(this, applicationContext)
        CoroutineScope(Dispatchers.IO).launch {
            if (!session.isLoggedIn()) {
                val i = Intent(applicationContext, LoginActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(i)
            }
        }
    }

    private fun logout() {
        session.endSession()
    }
}