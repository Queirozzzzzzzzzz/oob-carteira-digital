package com.oob.carteira_digital

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.oob.carteira_digital.databinding.FragmentMessageBinding
import com.oob.carteira_digital.databinding.FragmentMessagesBinding
import com.oob.carteira_digital.models.DBHelper
import com.oob.carteira_digital.models.MessageListAdapter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.*

class MessageFragment(private val messageId: String, private val title: String, private val content: String) : Fragment() {
    private var _binding: FragmentMessageBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: DBHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessageBinding.inflate(inflater, container, false)
        val view = binding.root
        db = DBHelper(requireContext())
        setMessage()
        return view
    }

    private fun setMessage() {
        db.updateNotificationReadStatus(messageId)
        binding.title.text = title
        binding.content.text = content
    }
}