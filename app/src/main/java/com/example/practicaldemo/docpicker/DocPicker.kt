package com.example.practicaldemo.docpicker

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.practicaldemo.R

class DocPicker : AppCompatActivity() {
    private lateinit var docPicker: Button
    private lateinit var imageView: ImageView

    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                // Handle the selected document URI
                handleDocumentUri(uri)
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_doc_picker)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        docPicker = findViewById(R.id.button)
        imageView = findViewById(R.id.image)

        docPicker.setOnClickListener {
            openFilePicker()

        }
    }
    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
//            type = "application/*"
            type = "*/*"
//            type = "images/*" // Set MIME type to select all types of documents
        }
        getContent.launch(intent)
    }
    private fun handleDocumentUri(uri: Uri) {
        // Handle the selected document URI here
        // For example, you can retrieve the file name
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            val fileName = cursor.getString(nameIndex)

            Log.d("FileName", ": $fileName")
            Log.d("FileName", "uri $uri")
            val filePath = FilePaths(this).getPathFromUri(uri)
            Log.d("FileName", ":file path: $filePath")

            // Now you have the file name, you can do further processing
            // such as displaying it in a TextView or performing operations on the file
        }
    }
}

