package com.oob.carteira_digital

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.oob.carteira_digital.databinding.FragmentMessagesBinding
import com.oob.carteira_digital.models.DBHelper
import com.oob.carteira_digital.models.MessageListAdapter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.*

class MessagesFragment : Fragment() {
    private var _binding: FragmentMessagesBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: DBHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessagesBinding.inflate(inflater, container, false)
        val view = binding.root
        db = DBHelper(requireContext())
        getNotifications()
        return view
    }

    private fun getNotifications() {
        val notifications = db.getNotifications()
        val updatedNotifications = mutableListOf<Map<String, Any?>>()

        for (notification in notifications) {
            val updatedNotification = notification.toMutableMap()
            if(notification["created_at"]!= null) {
                updatedNotification["created_at"] = getUiCreatedAt(notification["created_at"].toString())
            }
            updatedNotifications.add(updatedNotification)
        }
        val adapter = MessageListAdapter(requireContext(), updatedNotifications)

        binding.notifications.adapter = adapter
        adapter.notifyDataChanged()
    }

    private fun getUiCreatedAt(createdAtString: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val createdAt = LocalDateTime.parse(createdAtString, formatter)
        val now = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"))

        val diffInSeconds = ChronoUnit.SECONDS.between(createdAt, now)

        val minutesAgo = diffInSeconds / 60
        val hoursAgo = minutesAgo / 60
        val daysAgo = hoursAgo / 24
        val monthsAgo = daysAgo / 30

        return if (minutesAgo < 60) {
            "${minutesAgo}m atrás"
        } else if (hoursAgo < 24) {
            "${hoursAgo}h atrás"
        } else if (daysAgo < 30) {
            "${daysAgo}d atrás"
        } else {
            "${monthsAgo} mês atrás"
        }
    }
}