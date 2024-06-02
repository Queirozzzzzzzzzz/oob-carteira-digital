package com.oob.carteira_digital

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

open class BaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBaseBinding
    private lateinit var session: SessionManager
    private var vmNotification = VMNotification()
    private var hasMessages = false
    private val db = DBHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkReadMessages()
        setMessagesIcon()

        binding.bottomNavView.setOnItemSelectedListener { item ->
            val itemId = item.itemId
            when (itemId) {
                R.id.home -> {
                    checkReadMessages()
                    setMessagesIcon()
                    startSession(HomeFragment())
                }

                R.id.card -> {
                    checkReadMessages()
                    setMessagesIcon()
                    startSession(CardFragment())
                }

                R.id.qr_code -> {
                    checkReadMessages()
                    setMessagesIcon()
                    startSession(QrCodeFragment())
                }

                R.id.messages -> {
                    checkReadMessages()
                    setMessagesIcon()
                    startSession(MessagesFragment())
                }

                else -> {
                    checkReadMessages()
                    setMessagesIcon()
                    startSession(SettingsFragment())
                }
            }
            true
        }

        startSession(HomeFragment())

        if (Preferences.isStartup()) {
            CoroutineScope(Dispatchers.IO).launch {
                startBackgroundJob()
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
        checkReadMessages()
        setMessagesIcon()
        binding.bottomNavView.menu.findItem(R.id.card).isChecked = true

        startSession(CardFragment())
    }

    fun settingsFragment(view: View) {
        checkReadMessages()
        setMessagesIcon()
        binding.bottomNavView.menu.findItem(R.id.settings).isChecked = true
        startSession(SettingsFragment())
    }

    fun homeFragment(view: View) {
        checkReadMessages()
        setMessagesIcon()
        binding.bottomNavView.menu.findItem(R.id.home).isChecked = true
        startSession(HomeFragment())
    }

    fun qrCodeFragment(view: View) {
        checkReadMessages()
        setMessagesIcon()
        binding.bottomNavView.menu.findItem(R.id.qr_code).isChecked = true
        startSession(QrCodeFragment())
    }

    fun messagesFragment(view: View) {
        checkReadMessages()
        setMessagesIcon()
        binding.bottomNavView.menu.findItem(R.id.messages).isChecked = true
        startSession(MessagesFragment())
    }

    fun newCardFragment(view: View) {
        checkReadMessages()
        setMessagesIcon()
        binding.bottomNavView.menu.findItem(R.id.settings).isChecked = true
        startSession(NewCardFragment())
    }

    fun accountFragment(view: View) {
        checkReadMessages()
        setMessagesIcon()
        binding.bottomNavView.menu.findItem(R.id.settings).isChecked = true
        startSession(AccountFragment())
    }

    fun newPasswordFragment(view: View) {
        checkReadMessages()
        setMessagesIcon()
        binding.bottomNavView.menu.findItem(R.id.settings).isChecked = true
        startSession(NewPasswordFragment())
    }

    fun supportFragment(view: View) {
        checkReadMessages()
        setMessagesIcon()
        binding.bottomNavView.menu.findItem(R.id.settings).isChecked = true
        startSession(SupportFragment())
    }

    fun editAccountFragment(view: View) {
        checkReadMessages()
        setMessagesIcon()
        binding.bottomNavView.menu.findItem(R.id.settings).isChecked = true
        startSession(EditAccountFragment())
    }

    private fun startBackgroundJob() {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                getNotifications()
                delay(5000)
            }
        }
    }

    private fun checkReadMessages() {
        val unreadNotifications = db.getUnreadNotifications()
        hasMessages = unreadNotifications.isNotEmpty()
    }

    fun markAllAsRead(view: View) {
        db.markAllNotificationsAsRead()
        checkReadMessages()
        messagesFragment(view)
    }

    private suspend fun getNotifications() {
        val notifications = vmNotification.getNotifications()

        if (notifications.isNotEmpty()) {
            db.insertNotifications(notifications)
        }
    }

    private fun setMessagesIcon() {
        runOnUiThread {
            if (hasMessages) {
                binding.bottomNavView.menu.findItem(R.id.messages).icon =
                    AppCompatResources.getDrawable(this, R.drawable.messages_icon_active)
            } else {
                binding.bottomNavView.menu.findItem(R.id.messages).icon =
                    AppCompatResources.getDrawable(this, R.drawable.messages_icon)
            }
        }
    }

    fun openMessage(messageId: String, title: String, content: String, view: View) {
        startSession(MessageFragment(messageId, title, content))
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