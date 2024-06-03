package com.oob.carteira_digital

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.oob.carteira_digital.databinding.FragmentAccountBinding
import com.oob.carteira_digital.databinding.FragmentQrCodeBinding
import com.oob.carteira_digital.models.DBHelper
import com.oob.carteira_digital.viewmodels.VMAccount

class AccountFragment : Fragment() {
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: DBHelper
    private lateinit var vmAccount: VMAccount

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        val view = binding.root
        db = DBHelper(requireContext())
        vmAccount = VMAccount()
        setInfo()
        return view
    }

    private fun setInfo() {
        val info = db.getAccount()

        binding.email.text = info["email"]
        binding.birthDate.text = info["birth_date"]
        binding.registration.text = info["registration"]
        binding.cpf.text = info["cpf"]

        if (info["is_student"] == "1") {
            binding.levelTitle.visibility = View.VISIBLE
            binding.courseTitle.visibility = View.VISIBLE
            binding.cClassTitle.visibility = View.VISIBLE

            var coursesRaw = info["courses"]
            if (coursesRaw != null) {
                val courses = vmAccount.getCourses(coursesRaw)

                binding.level.text = info["level"]
                binding.course.text = courses[0].course
                binding.cClass.text = courses[0].c_class
            }
        }
    }


}