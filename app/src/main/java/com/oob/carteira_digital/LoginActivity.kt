package com.oob.carteira_digital

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.oob.carteira_digital.databinding.ActivityLoginBinding
import com.oob.carteira_digital.models.DBHelper
import com.oob.carteira_digital.models.SessionManager
import com.oob.carteira_digital.objects.Preferences
import com.oob.carteira_digital.viewmodels.VMAccount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.job
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var session: SessionManager
    private lateinit var viewModel: VMAccount
    private var db = DBHelper(this)

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
        }
    }

    override fun onResume() {
        super.onResume()
        if (session.isLoggedIn()) {
            session.startSession()
        }
    }

    private fun startSession() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val info = viewModel.getInfo()
                db.saveAccount(info)
                session.startSession()
            } catch (e: Exception) {
                session.clearSession()
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
                val result = viewModel.login(cpf, password)
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
}