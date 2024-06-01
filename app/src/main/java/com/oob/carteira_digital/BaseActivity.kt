package com.oob.carteira_digital

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.oob.carteira_digital.databinding.ActivityBaseBinding
import com.oob.carteira_digital.models.DBHelper
import com.oob.carteira_digital.models.SessionManager
import com.oob.carteira_digital.objects.Preferences
import com.oob.carteira_digital.viewmodels.VMNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class BaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBaseBinding
    private lateinit var session: SessionManager
    private var vmNotification = VMNotification()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavView.setOnItemSelectedListener { item ->
            val itemId = item.itemId
            when (itemId) {
                R.id.home -> {
                    startSession(HomeFragment())
                }

                R.id.card -> {
                    startSession(CardFragment())
                }

                R.id.qr_code -> {
                    startSession(QrCodeFragment())
                }

                R.id.messages -> {
                    startSession(MessagesFragment())
                }

                else -> {
                    startSession(SettingsFragment())
                }
            }
            true
        }

        startSession(HomeFragment())

        if (Preferences.isStartup()) {
            CoroutineScope(Dispatchers.IO).launch {
                getNotifications()
            }
        }

        session = SessionManager(this, applicationContext)
        checkLogin()

        Preferences.setStartup(false)
    }

    private fun startSession(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.frameLayout, fragment)

        fragmentTransaction.commit()
    }


    // FUNÇÃO MUDAR ACTIVITY POR CARDVIEW
    fun cardFragment(view: View) {
        binding.bottomNavView.menu.findItem(R.id.card).isChecked = true

        startSession(CardFragment())
    }

    fun settingsFragment(view: View) {
        binding.bottomNavView.menu.findItem(R.id.settings).isChecked = true
        startSession(SettingsFragment())
    }

    fun homeFragment(view: View) {
        binding.bottomNavView.menu.findItem(R.id.home).isChecked = true
        startSession(HomeFragment())
    }

    fun qrCodeFragment(view: View) {
        binding.bottomNavView.menu.findItem(R.id.qr_code).isChecked = true
        startSession(QrCodeFragment())
    }

    fun messagesFragment(view: View) {
        binding.bottomNavView.menu.findItem(R.id.messages).isChecked = true
        startSession(MessagesFragment())
    }

    fun newCardFragment(view: View) {
        binding.bottomNavView.menu.findItem(R.id.settings).isChecked = true
        startSession(NewCardFragment())
    }

    fun accountFragment(view: View) {
        binding.bottomNavView.menu.findItem(R.id.settings).isChecked = true
        startSession(AccountFragment())
    }

    fun newPasswordFragment(view: View) {
        binding.bottomNavView.menu.findItem(R.id.settings).isChecked = true
        startSession(NewPasswordFragment())
    }

    fun supportFragment(view: View) {
        binding.bottomNavView.menu.findItem(R.id.settings).isChecked = true
        startSession(SupportFragment())
    }

    fun editAccountFragment(view: View) {
        binding.bottomNavView.menu.findItem(R.id.settings).isChecked = true
        startSession(EditAccountFragment())
    }

    private suspend fun getNotifications() {
        val db = DBHelper(this)
        var notifications = vmNotification.getNotifications()
        if (notifications.isNotEmpty()) {
            db.insertNotifications(notifications)
        }

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