package com.oob.carteira_digital

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.oob.carteira_digital.databinding.ActivityLoginBinding
import com.oob.carteira_digital.models.SessionManager
import com.oob.carteira_digital.objects.Preferences
import com.oob.carteira_digital.viewmodels.VMAccount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var session: SessionManager
    private lateinit var viewModel: VMAccount

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Preferences.setup(applicationContext)
        viewModel = ViewModelProvider(this)[VMAccount::class.java]
        session = SessionManager(this, applicationContext)

        if (session.isLoggedIn()) {
            session.startSession()
        } else {
            setLogin()
            checkBiometricSupported()
        }
    }

    override fun onResume() {
        super.onResume()
        if (session.isLoggedIn()) {
            session.startSession()
        }
    }

    private fun setLogin() {
        binding.loginButton.setOnClickListener {
            val cpf = binding.login.text.toString()
            val password = binding.password.text.toString()

            CoroutineScope(Dispatchers.IO).launch {
                val result = viewModel.login(cpf, password)
                runOnUiThread {
                    if (result != "true") {
                        Toast.makeText(applicationContext, result, Toast.LENGTH_SHORT).show()
                    } else {
                        session.startSession()
                    }
                }
            }
        }
    }

    private fun checkBiometricSupported() {
        val biometricManager = BiometricManager.from(this)

        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> biometricLogin()
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE, BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE, BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> binding.fingerprint.setVisibility(
                View.GONE
            )

            else -> {}
        }
    }

    private fun biometricLogin() {
        val executor = ContextCompat.getMainExecutor(this)

        val biometricPrompt =
            BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    CoroutineScope(Dispatchers.IO).launch {
                        val result = viewModel.biometricLogin()
                        runOnUiThread {
                            if (result != "true") {
                                Toast.makeText(applicationContext, result, Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                session.startSession()
                            }
                        }
                    }
                }
            })

        val promptInfo = PromptInfo.Builder().setTitle("OOB Carteira Digital")
            .setDescription("Use sua digital para entrar").setNegativeButtonText("Cancelar").build()

        biometricPrompt.authenticate(promptInfo)

        binding.fingerprint.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }
    }
}