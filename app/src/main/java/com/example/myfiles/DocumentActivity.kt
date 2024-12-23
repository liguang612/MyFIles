package com.example.myfiles

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myfiles.databinding.ActivityDocumentBinding
import java.io.File

class DocumentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDocumentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocumentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        val path = intent.getStringExtra("path")

        if (path != null) {
            try {
//                val writer = File(path).outputStream().writer()
//                writer.write(binding.tvContent.text.toString())
//
//                writer.close()
                val text = File(path).readText()
                binding.tvContent.text = text
            } catch (e: Exception) {
                Toast.makeText(this, "Có lỗi xảy ra!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}