package com.oob.carteira_digital

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.oob.carteira_digital.databinding.FragmentNewPasswordBinding
import com.oob.carteira_digital.databinding.FragmentQrCodeBinding
import com.oob.carteira_digital.models.SessionManager
import com.oob.carteira_digital.viewmodels.VMAccount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewPasswordFragment : Fragment() {
    private var _binding: FragmentNewPasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var session: SessionManager
    private val vmaccount = VMAccount()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPasswordBinding.inflate(inflater, container, false)
        val view = binding.root
        session = SessionManager(this, requireContext())

        binding.btnSubmit.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                resetPassword()
            }
        }
        return view
    }

    private suspend fun resetPassword() {
        val newPassword = binding.newPassword.text.toString()
        val confirmPassword = binding.confirmNewPassword.text.toString()

        val result = vmaccount.resetPassword(newPassword, confirmPassword)
        if (result == "true") {
            session.endSession()
        } else {
            Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
        }
    }
}