package com.oob.carteira_digital

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.oob.carteira_digital.databinding.FragmentSettingsBinding
import com.oob.carteira_digital.models.SessionManager
import com.oob.carteira_digital.objects.Preferences

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var session: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        session = context?.let { SessionManager(this, it) }!!
        val view = binding.root
        setSwitchTheme()
        binding.leave.setOnClickListener { session.endSession() }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setSwitchTheme() {
        val isChecked = Preferences.isDarkTheme()
        binding.switchTheme.isChecked = isChecked
        binding.switchTheme.setOnClickListener { this.switchTheme() }
    }

    private fun switchTheme() {
        val isChecked = binding.switchTheme.isChecked
        if (isChecked) {
            Preferences.setDarkTheme(true)
            session.setTheme()
        } else {
            Preferences.setDarkTheme(false)
            session.setTheme()
        }

    }


}
