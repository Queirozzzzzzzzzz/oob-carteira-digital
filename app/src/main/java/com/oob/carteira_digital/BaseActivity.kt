package com.oob.carteira_digital

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.oob.carteira_digital.databinding.ActivityBaseBinding
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
                R.id.home -> {
                    startSession(HomeFragment(), false)
                }

                R.id.card -> {
                    startSession(CardFragment(), false)
                }

                R.id.qr_code -> {
                    startSession(QrCodeFragment(), false)
                }

                R.id.messages -> {
                    startSession(MessagesFragment(), false)
                }

                else -> {
                    startSession(SettingsFragment(), false)
                }
            }
            true
        }

        startSession(HomeFragment(), true)
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

    // FUNÇÃO MUDAR ACTIVITY POR CARDVIEW
    fun cardFragment(view: View) {
        binding.bottomNavView.menu.findItem(R.id.card).isChecked = true

        startSession(CardFragment(), false)
    }

    fun settingsFragment(view: View) {
        binding.bottomNavView.menu.findItem(R.id.settings).isChecked = true
        startSession(SettingsFragment(), false)
    }

    fun homeFragment(view: View) {
        binding.bottomNavView.menu.findItem(R.id.home).isChecked = true
        startSession(HomeFragment(), false)
    }

    fun qrCodeFragment(view: View) {
        binding.bottomNavView.menu.findItem(R.id.qr_code).isChecked = true
        startSession(QrCodeFragment(), false)
    }

    fun messagesFragment(view: View) {
        binding.bottomNavView.menu.findItem(R.id.messages).isChecked = true
        startSession(MessagesFragment(), false)
    }

    fun newCardFragment(view: View) {
        binding.bottomNavView.menu.findItem(R.id.settings).isChecked = true
        startSession(NewCardFragment(), false)
    }

    fun accountFragment(view: View) {
        binding.bottomNavView.menu.findItem(R.id.settings).isChecked = true
        startSession(AccountFragment(), false)
    }

    fun newPasswordFragment(view: View) {
        binding.bottomNavView.menu.findItem(R.id.settings).isChecked = true
        startSession(NewPasswordFragment(), false)
    }

    fun supportFragment(view: View) {
        binding.bottomNavView.menu.findItem(R.id.settings).isChecked = true
        startSession(SupportFragment(), false)
    }

    fun editAccountFragment(view: View) {
        binding.bottomNavView.menu.findItem(R.id.settings).isChecked = true
        startSession(EditAccountFragment(), false)
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