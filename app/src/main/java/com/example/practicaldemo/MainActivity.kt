package com.example.practicaldemo

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Window
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.practicaldemo.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    @SuppressLint("SdCardPath")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Instantiate the player.
//        val player = ExoPlayer.Builder(this).build()
//// Attach player to the view.
//        binding.playerView.player = player
//// Set the media item to be played.
//        player.setMediaItem(mediaItem)
//// Prepare the player.
//        player.prepare()

//        val videoPath = "/storage/emulated/0/sdcard/.transforms/synthetic/picker/0/com.android.providers.media.photopicker/media/1000000786.mp4"
//        val uriPath = getPathFromUri(this, videoPath.toUri())
//        val durationInMillis = getVideoDuration(uriPath!!)
//        val durationInSeconds = durationInMillis / 1000
//        Log.d("Video duration:", " ${durationInSeconds}s")
    }

    private fun getVideoDuration(videoPath: String): Long {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(videoPath)
        val durationString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        retriever.release()

        return durationString?.toLongOrNull() ?: 0L
    }

    fun getPathFromUri(context: Context, uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        val path = cursor?.getString(columnIndex ?: 0)
        cursor?.close()
        return path
    }

}