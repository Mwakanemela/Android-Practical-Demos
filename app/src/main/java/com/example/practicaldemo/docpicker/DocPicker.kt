package com.example.practicaldemo.docpicker

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.practicaldemo.R
import com.tom_roush.pdfbox.pdmodel.PDDocument
import java.io.File
import java.io.InputStream
import java.util.Locale
import kotlin.math.log10
import kotlin.math.pow

class DocPicker : AppCompatActivity() {
    private lateinit var docPicker: Button
    private lateinit var imageView: ImageView
    private lateinit var docTitle: TextView
    private lateinit var docInfo: TextView

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
        docTitle = findViewById(R.id.docTitle)
        docInfo = findViewById(R.id.docInfo)

        docPicker.setOnClickListener {
            openFilePicker()

        }
    }
    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/*"
//            type = "*/*"
//            type = "images/*" // Set MIME type to select all types of documents
        }
        getContent.launch(intent)
    }
    @SuppressLint("SetTextI18n")
    private fun handleDocumentUri(uri: Uri) {
        // Handle the selected document URI here
        // For example, you can retrieve the file name
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)

            cursor.moveToFirst()
            val fileName = cursor.getString(nameIndex)
            val fileSize = cursor.getLong(sizeIndex)
            docTitle.text = fileName
//            val inputStream = assets.open(uri.toString())
//            val document = PDDocument.load(inputStream)
            val numberOfPages = getNumberOfPagesFromUri(this, uri)

            val fileSizes = formatFileSize(fileSize)

            val documentType = documentType(fileName)
            docInfo.text = "$numberOfPages pages . $fileSizes . $documentType"


            Log.d("FileName", ": $fileName")
            Log.d("FileName", "uri $uri")
//            val filePath = FilePaths(this).getPathFromUri(uri)
//            Log.d("FileName", ":file path: $filePath")
            val fileExists = isFileExists(this, uri)
            Log.d("FileName", ": fileExists $fileExists")

//            document.close()

            // Now you have the file name, you can do further processing
            // such as displaying it in a TextView or performing operations on the file
        }
    }

//    fun isFileExists(context: Context, uri: Uri): Boolean {
//        val filePath = FilePaths(context).getPathFromUri(uri)
//        Log.d("FileName", "file path $filePath")
//        return filePath?.let { File(it).exists() } ?: false
//    }

    fun isFileExists(context: Context, uri: Uri): Boolean {
        try {
            val documentId = DocumentsContract.getDocumentId(uri)
            val contentUri = DocumentsContract.buildDocumentUri("com.android.providers.media.documents", documentId)
            val cursor = context.contentResolver.query(contentUri, null, null, null, null)
            return cursor?.use {
                it.moveToFirst()
                it.count > 0 // Check if the cursor contains any data
            } ?: false
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            return false
        }
    }


    private fun getNumberOfPagesFromUri(context: Context, uri: Uri): Int {
        var inputStream: InputStream? = null
        var numberOfPages = 0
        try {
            inputStream = context.contentResolver.openInputStream(uri)
            if (inputStream != null) {
                val document = PDDocument.load(inputStream)
                numberOfPages = document.numberOfPages
                document.close()
            }
        } catch (e: Exception) {
            // Handle exceptions
            e.printStackTrace()
        } finally {
            inputStream?.close()
        }
        return numberOfPages
    }

    @SuppressLint("DefaultLocale")
    fun formatFileSize(sizeBytes: Long): String {
        if (sizeBytes <= 0) return "0 B"

        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (log10(sizeBytes.toDouble()) / log10(1024.0)).toInt()

        return String.format("%.2f %s", sizeBytes / 1024.0.pow(digitGroups.toDouble()), units[digitGroups])
    }

//    fun getDocumentTypeFromUri(uri: Uri): String? {
//        val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
//        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
//    }

    private fun documentType(fileName: String) : String {
        val extension = fileName.substringAfterLast(".", "Unknown")
        return extension
    }
    private fun getDocumentTypeFromUri(uri: Uri): String {
//        val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString()) ?: return "Unknown"
        val fileName = uri.lastPathSegment ?: return "Unknown"
        val extension = fileName.substringAfterLast(".", "Unknown")
        return extension
        // Manually map common file extensions to MIME types
//        return when (extension.lowercase(Locale.getDefault())) {
//            "pdf" -> "application/pdf"
//            "doc", "docx" -> "application/msword"
//            "xls", "xlsx" -> "application/vnd.ms-excel"
//            "ppt", "pptx" -> "application/vnd.ms-powerpoint"
//            "txt" -> "text/plain"
//            // Add more mappings as needed
//            else -> "Unknown"
//        }
    }


}

