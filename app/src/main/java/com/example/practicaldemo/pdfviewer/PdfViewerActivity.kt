package com.example.practicaldemo.pdfviewer

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.practicaldemo.R
import com.example.practicaldemo.databinding.ActivityPdfViewerBinding

class PdfViewerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfViewerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfViewerBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.morePdf.setOnClickListener {
            launchPDF.launch("application/*")
        }
    }

    private val launchPDF = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) {
        uri->
        uri?.let {
            binding.pdfViewer.fromUri(it)
                .spacing(12)
                .defaultPage(0)
                .enableAnnotationRendering(false)
                .enableDoubletap(true)
                .load()
            binding.pdfViewer.fitToWidth()
            binding.pdfViewer.useBestQuality(true)
        }
    }
}