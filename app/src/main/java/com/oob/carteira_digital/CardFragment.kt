package com.oob.carteira_digital

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.oob.carteira_digital.databinding.FragmentCardBinding
import com.oob.carteira_digital.models.DBHelper


class CardFragment : Fragment() {
    private var _binding: FragmentCardBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: DBHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCardBinding.inflate(inflater, container, false)
        val view = binding.root
        db = DBHelper(requireContext())
        setCard()
        return view
    }

    data class Course(val id: Double, val course: String, val cClass: String)

    @SuppressLint("SetTextI18n")
    private fun setCard() {
        val info = db.getAccount()

        binding.institution.text = info["institution"]
        binding.fullName.text = "Nome: ${info["full_name"]}"
        binding.cpf.text = "CPF: ${info["cpf"]}"
        binding.birthDate.text = "Data de Nascimento: ${info["birth_date"]}"

        var courses = info["courses"]
        if (courses != null) {
            courses = courses.replace("class", "cClass")
            val nCourses: List<Course> =
                Gson().fromJson(courses, object : TypeToken<List<Course?>?>() {}.type)

            binding.course.text = "Curso: ${nCourses[0].course}"
            binding.cClass.text = "Turma: ${nCourses[0].cClass}"
        }

        info["registration"]?.let { setQrCode(it) }
    }

    private fun setQrCode(registration: String) {
        val mfw = MultiFormatWriter()
        val be = BarcodeEncoder()

        val bm = mfw.encode(registration, BarcodeFormat.CODE_39, 110, 75, null)
        val bitmap = be.createBitmap(bm)
        binding.barcode.setImageBitmap(bitmap)
    }
}