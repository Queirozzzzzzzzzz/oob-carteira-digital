package com.oob.carteira_digital

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.oob.carteira_digital.databinding.FragmentQrCodeBinding
import com.oob.carteira_digital.models.DBHelper

class QrCodeFragment : Fragment() {
    private var _binding: FragmentQrCodeBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: DBHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQrCodeBinding.inflate(inflater, container, false)
        val view = binding.root
        db = DBHelper(requireContext())
        setQrCode()
        return view
    }

    private fun setQrCode() {
        val registration = db.getRegistration()
        val mfw = MultiFormatWriter()
        val be = BarcodeEncoder()

        val bm = mfw.encode(registration, BarcodeFormat.QR_CODE, 512, 512, null)
        val bitmap = be.createBitmap(bm)
        binding.qrCode.setImageBitmap(bitmap)
    }
}