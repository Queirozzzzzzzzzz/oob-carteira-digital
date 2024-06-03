package com.oob.carteira_digital

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.oob.carteira_digital.databinding.ActivityLoginBinding
import com.oob.carteira_digital.interfaces.InternetConnectionCallback
import com.oob.carteira_digital.models.DBHelper
import com.oob.carteira_digital.models.SessionManager
import com.oob.carteira_digital.objects.InternetConnectionObserver
import com.oob.carteira_digital.objects.Preferences
import com.oob.carteira_digital.viewmodels.VMAccount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.job
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity(), InternetConnectionCallback {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var session: SessionManager
    private lateinit var viewModel: VMAccount
    private var db = DBHelper(this)
    private var isConnected = true

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Preferences.setup(applicationContext)
        viewModel = ViewModelProvider(this)[VMAccount::class.java]
        session = SessionManager(this, applicationContext)

        InternetConnectionObserver.instance(this).setCallback(this).register()

        session.setTheme()

        if (session.isLoggedIn()) {
            session.startSession()
        } else {
            setLogin()
            checkBiometricSupported()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        InternetConnectionObserver.unRegister()
    }

    private fun startSession() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (isConnected) {
                    val info = viewModel.getInfo()
                    db.saveAccount(info)
                }

                session.startSession()
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(applicationContext, "Houve um erro interno.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun setLogin() {
        binding.loginButton.setOnClickListener {
            val cpf = binding.login.text.toString()
            val password = binding.password.text.toString()

            CoroutineScope(Dispatchers.IO).launch {
                val dialogJob = launch {
                    try {
                        showLoadingDialog()
                        coroutineContext.job.join()
                    } finally {
                        hideLoadingDialog()
                    }
                }
                var result = ""
                if (isConnected) {
                    result = viewModel.login(cpf, password)
                } else {
                    result = viewModel.offlineLogin(cpf, password)
                }
                dialogJob.cancel()
                runOnUiThread {
                    if (result != "true") {
                        Toast.makeText(applicationContext, result, Toast.LENGTH_SHORT).show()
                    } else {
                        startSession()
                    }
                }
            }
        }
    }

    private fun checkBiometricSupported() {
        if (Preferences.getAuthCpf().isEmpty() || Preferences.getAuthPassword().isEmpty()) {
            return
        }

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
                        val dialogJob = launch {
                            try {
                                showLoadingDialog()
                                coroutineContext.job.join()
                            } finally {
                                hideLoadingDialog()
                            }
                        }
                        var result = ""
                        if (isConnected) {
                            result = viewModel.biometricLogin()
                        } else {
                            result = viewModel.offlineBiometricLogin()
                        }
                        dialogJob.cancel()
                        runOnUiThread {
                            if (result != "true") {
                                Toast.makeText(applicationContext, result, Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                startSession()
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

    private fun showLoadingDialog() {
        runOnUiThread {
            binding.loadingBg.visibility = View.VISIBLE
            binding.loading.visibility = View.VISIBLE
        }
    }

    private fun hideLoadingDialog() {
        runOnUiThread {
            binding.loadingBg.visibility = View.GONE
            binding.loading.visibility = View.GONE
        }
    }

    override fun onConnected() {
        isConnected = true
    }

    override fun onDisconnected() {
        isConnected = false
        Toast.makeText(this, "Sem conexão com a internet!", Toast.LENGTH_SHORT).show()
    }
}