package com.oob.carteira_digital

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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

        checkLogin()
        setLogin()
    }

    private fun checkLogin() {
        session = SessionManager(this, applicationContext)
        CoroutineScope(Dispatchers.IO).launch {
            if (session.isLoggedIn()) {
                val i = Intent(applicationContext, MainActivity2::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(i)
            }
        }
    }

    private fun setLogin() {
        binding.loginButton.setOnClickListener {
            val cpf = binding.login.text.toString()
            val password = binding.password.text.toString()

            CoroutineScope(Dispatchers.IO).launch {
                val result = viewModel.login(cpf, password)
                runOnUiThread {
                    when (result) {
                        "incorrect" -> Toast.makeText(
                            applicationContext,
                            "Campos inválidos!",
                            Toast.LENGTH_SHORT
                        ).show()
                        "Complete os campos em branco." -> Toast.makeText(
                            applicationContext,
                            result,
                            Toast.LENGTH_SHORT
                        ).show()
                        else -> {
                            Log.d("LOGIN_FAIL", result)
                            session.createSession(result)
                            Toast.makeText(
                                applicationContext,
                                "Logado com sucesso",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }
}