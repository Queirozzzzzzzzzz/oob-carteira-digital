package com.oob.carteira_digital

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.oob.carteira_digital.databinding.ActivityBaseBinding
import com.oob.carteira_digital.databinding.ActivityLoginBinding
import com.oob.carteira_digital.models.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class BaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBaseBinding
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavView.setOnItemSelectedListener { item ->
            val itemId = item.itemId
            when (itemId) {
                R.id.Home -> {
                    startSession(HomeActivity(), false)
                }
                R.id.Card -> {
                    startSession(CardActivity(), false)
                }
                R.id.qrcode -> {
                    startSession(QrCodeActivity(), false)
                }
                R.id.Notification -> {
                    startSession(NotificationActivity(), false)
                }
                else -> {
                    startSession(SettingsActivity(), false)
                }
            }
            true
        }

        startSession(HomeActivity(), true)
        session = SessionManager(this, applicationContext)
        checkLogin()
    }

    private fun startSession(fragment: Fragment, isAppInitialized: Boolean) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        if (isAppInitialized) {
            fragmentTransaction.add(R.id.frameLayout, fragment)
        } else {
            fragmentTransaction.replace(R.id.frameLayout, fragment)
        }

        fragmentTransaction.commit()
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