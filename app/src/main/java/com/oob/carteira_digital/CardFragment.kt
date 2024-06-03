package com.oob.carteira_digital

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.oob.carteira_digital.databinding.FragmentCardBinding
import com.oob.carteira_digital.models.DBHelper
import com.oob.carteira_digital.viewmodels.VMAccount

class CardFragment : Fragment() {
    private var _binding: FragmentCardBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: DBHelper
    private lateinit var vmAccount: VMAccount

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCardBinding.inflate(inflater, container, false)
        val view = binding.root
        db = DBHelper(requireContext())
        vmAccount = VMAccount()

        isValid()
        return view
    }

    data class Course(
        val id: Double,
        val course: String,
        val c_class: String,
        val enter_time: String,
        val leave_time: String,
        val end_date: String,
        val days: String
    )

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
            val courses = vmAccount.getCourses(info["courses"]!!)

            binding.course.text = "Curso: ${courses[0].course}"
            binding.cClass.text = "Turma: ${courses[0].c_class}"
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

        val bm = mfw.encode(registration, BarcodeFormat.CODE_39, 110, 75, null)
        val bitmap = be.createBitmap(bm)
        binding.barcode.setImageBitmap(bitmap)
    }
}