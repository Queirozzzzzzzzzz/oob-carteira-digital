package com.oob.carteira_digital

import android.os.Bundle
import android.widget.Button
import com.oob.carteira_digital.models.SessionManager

class MainActivity2 : BaseActivity() {
    private lateinit var session: SessionManager
    private lateinit var logoutBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        session = SessionManager(this, applicationContext)
        logoutBtn = findViewById(R.id.logout)
        logoutBtn.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        session.endSession()
    }
}