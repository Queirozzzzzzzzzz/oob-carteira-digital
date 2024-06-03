package com.oob.carteira_digital

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.oob.carteira_digital.databinding.FragmentEditAccountBinding
import java.io.ByteArrayOutputStream

class EditAccountFragment : Fragment() {
    private var _binding: FragmentEditAccountBinding? = null
    private val binding get() = _binding!!
    private lateinit var binaryFile: ByteArray

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditAccountBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.iconCamera.setOnClickListener {
            getPhoto()
        }

        return view
    }

    private fun getPhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, 100)
        } else {
            Log.w("EditAccountFragment", "No app to pick image")
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data ?: return
            val size = Size(128, 128)
            val bitmap = requireActivity().contentResolver.loadThumbnail(imageUri, size, null)

            if (bitmap != null) {
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                binaryFile = byteArrayOutputStream.toByteArray()
            }
        }
    }


}