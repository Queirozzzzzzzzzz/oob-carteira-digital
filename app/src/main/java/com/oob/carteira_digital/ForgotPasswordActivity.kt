package com.oob.carteira_digital

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.oob.carteira_digital.databinding.ActivityForgotPasswordBinding
import com.oob.carteira_digital.databinding.ActivityLoginBinding
import com.oob.carteira_digital.models.DBHelper
import com.oob.carteira_digital.models.SessionManager
import com.oob.carteira_digital.objects.InternetConnectionObserver
import com.oob.carteira_digital.objects.Preferences
import com.oob.carteira_digital.viewmodels.VMAccount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private val vmAccount = VMAccount()

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE
        );
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.submit.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                sendEmail()
            }
        }
    }

    private suspend fun sendEmail(): Boolean {
        try {
            vmAccount.forgotPassword(binding.email.text.toString())
            runOnUiThread {
                binding.title.visibility = View.GONE
                binding.submit.visibility = View.GONE
                binding.email.visibility = View.GONE

                binding.message.text = "E-mail enviado. Confira sua caixa de emails!"
            }
        } catch (e: Exception) {
            runOnUiThread {
                Toast.makeText(this, "Ocorreu uma falha interna.", Toast.LENGTH_SHORT).show()
            }
        }

        return true
    }

}