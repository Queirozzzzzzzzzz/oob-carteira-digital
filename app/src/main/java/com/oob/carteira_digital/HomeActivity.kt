package com.oob.carteira_digital

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.oob.carteira_digital.models.DBHelper
import com.oob.carteira_digital.models.SessionManager

class HomeActivity : Fragment() {
    private lateinit var session: SessionManager
    private lateinit var logoutBtn: Button
    private var db = context?.let { DBHelper(it) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.activity_home, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("HomeActivity", db?.getAccount().toString())
    }
}