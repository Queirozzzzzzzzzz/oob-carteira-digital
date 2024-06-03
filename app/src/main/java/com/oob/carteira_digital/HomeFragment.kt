package com.oob.carteira_digital

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.oob.carteira_digital.databinding.FragmentHomeBinding
import com.oob.carteira_digital.models.DBHelper
import com.oob.carteira_digital.viewmodels.VMAccount

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: DBHelper
    private lateinit var vmAccount: VMAccount

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        db = DBHelper(requireContext())
        vmAccount = VMAccount()

        isValid()

        return view
    }

    private fun isValid() {
        val account = db.getAccount()
        var isValid = false

        isValid = if (account["is_student"] == "0") {
            false
        } else {
            vmAccount.isValidCourse(account["courses"]!!)
        }

        setCard(account, isValid)
    }

    @SuppressLint("SetTextI18n")
    private fun setCard(info: Map<String, String>, isValid: Boolean) {
        binding.institution.text = info["institution"]
        binding.fullName.text = "Nome: ${info["full_name"]}"
        binding.cpf.text = "CPF: ${info["cpf"]}"
        binding.birthDate.text = "Data de Nascimento: ${info["birth_date"]}"

        if (isValid) {
            var courses = info["courses"]!!
            courses = courses.replace("class", "cClass")
            val nCourses: List<CardFragment.Course> =
                Gson().fromJson(courses, object : TypeToken<List<CardFragment.Course?>?>() {}.type)

            binding.course.text = "Curso: ${nCourses[0].course}"
        }

        if (info["is_student"] == "1") {
            if (isValid) {
                info["registration"]?.let { setQrCode(it) }
            }
        } else {
            info["registration"]?.let { setQrCode(it) }
        }
    }

    private fun setQrCode(registration: String) {
        val mfw = MultiFormatWriter()
        val be = BarcodeEncoder()

        val bm = mfw.encode(registration, BarcodeFormat.CODE_39, 70, 70, null)
        val bitmap = be.createBitmap(bm)
        binding.cardQrCode.setImageBitmap(bitmap)
    }

}