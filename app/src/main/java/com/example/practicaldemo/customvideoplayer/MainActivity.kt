package com.example.practicaldemo.customvideoplayer

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.practicaldemo.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Formatter
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private lateinit var imageView: ImageView
    private lateinit var seekBar: SeekBar
    private lateinit var finalDuration: TextView
    private lateinit var currentDuration: TextView
    private var isPlaying = false
    private var isUserSeeking = false
    private val playbackStateListener: Player.Listener = playbackStateListener()
    private fun updateSeekBar() {
        player?.let { player ->
            if (!isUserSeeking) {
                val currentPosition = player.currentPosition.toInt()
                seekBar.progress = currentPosition
            }
        }
    }
    @SuppressLint("SetTextI18n")
    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView)

        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, windowInsets ->

            if (windowInsets.isVisible(WindowInsetsCompat.Type.statusBars())) {
                windowInsetsController.hide(WindowInsetsCompat.Type.statusBars())
            }

            ViewCompat.onApplyWindowInsets(view, windowInsets)
        }
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        playerView = findViewById(R.id.player_view)
        imageView = findViewById(R.id.imageView)
        seekBar = findViewById(R.id.seekBar)
        finalDuration = findViewById(R.id.finalDurationTextView)
        currentDuration = findViewById(R.id.currentDurationTextView)

        player = ExoPlayer.Builder(this).build()

        playerView.player = player

        // Disable default controls
        playerView.useController = false
        val mediaItem =
            MediaItem.fromUri("https://media.geeksforgeeks.org/wp-content/uploads/20201217163353/Screenrecorder-2020-12-17-16-32-03-350.mp4")

        player!!.setMediaItem(mediaItem)

        player!!.prepare()

        player!!.play()
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    player?.seekTo(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        player?.repeatMode = Player.REPEAT_MODE_ONE
        player?.addListener(playbackStateListener)


        // Update seek bar progress
        player?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                super.onPlaybackStateChanged(state)
                if (state == Player.STATE_READY) {
                    seekBar.max = player?.duration?.toInt()!!
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onPositionDiscontinuity(reason: Int) {
                updateSeekBar()
            }
//            @Deprecated("Deprecated in Java")
//            override fun onPositionDiscontinuity(reason: Int) {
//                super.onPositionDiscontinuity(reason)
//                seekBar.progress = player!!.currentPosition.toInt()
//            }
        })

        // Start updating seek bar periodically
//        updateSeekBarHandler = Handler(Looper.getMainLooper())
//        updateSeekBarHandler?.postDelayed(updateSeekBarRunnable, SEEK_BAR_UPDATE_INTERVAL_MS)

        playerView.setOnClickListener {
            isPlaying = !isPlaying
            if (isPlaying) {
                imageView.visibility = View.VISIBLE
                imageView.setImageResource(R.drawable.play_button_icon)
                player?.pause()
            } else {
                imageView.visibility = View.GONE
//                imageView.setImageResource(R.drawable.pause_button_icon)
                player?.play()
            }

        }


    }

    override fun onDestroy() {
        super.onDestroy()
        player?.removeListener(playbackStateListener)
        player?.release()
    }
    private fun playbackStateListener() = object : Player.Listener {
        @SuppressLint("SetTextI18n")
        override fun onPlaybackStateChanged(state: Int) {
            when (state) {
                ExoPlayer.STATE_ENDED -> {
                }
                Player.STATE_BUFFERING -> {

                }

                Player.STATE_IDLE -> {
                }

                Player.STATE_READY -> {

                    Log.d("TAG", "STATE_READY")
                    val durationMs: Long? = player?.duration
//        val formattedDuration = formatDuration(durationMs!!)
//        finalDuration.text = formattedDuration
                    if (durationMs != null) {
                        val formattedDuration = formatDuration(durationMs)
                        finalDuration.text = formattedDuration
                    } else {
                        // Handle the case where duration is null
                        finalDuration.text = "Duration not available"
                    }
                    startUpdatingSeekBar()
                }

                else -> {
                    Log.d("TAG", "STOP SEEK BAR")
                    stopUpdatingSeekBar()
                }
            }
        }

        private var updateSeekBarJob: Job? = null

        private fun startUpdatingSeekBar() {
            updateSeekBarJob = CoroutineScope(Dispatchers.Main).launch {
                while (true) {

                    updateSeekBar()
                    delay(50) // Update seek bar every second (adjust as needed)
                    val currentPositionMs = player?.currentPosition ?: 0

                    // Format current playback position into "mm:ss" format
                    val formattedPosition = formatDuration(currentPositionMs)

                    // Update TextView with formatted playback position
                    currentDuration.text = formattedPosition

                }
            }
        }

        private fun stopUpdatingSeekBar() {
            updateSeekBarJob?.cancel()
        }

        override fun onIsPlayingChanged(isVideoPlaying: Boolean) {
//        super.onIsPlayingChanged(isPlaying)

        }

        override fun onEvents(player: Player, events: Player.Events) {
//        super.onEvents(player, events)
//            if (events.contains(Player.EVENT_PLAYBACK_STATE_CHANGED) ||
//                events.contains(Player.EVENT_IS_PLAYING_CHANGED)
//            ) {
//
////                progressBar.visibility = View.GONE
//            }
//
//            if (events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION)
//            ) {
////                player.seekTo(5000L)
//            }
        }
    }

//    private fun formatDuration(milliseconds: Long?): String {
//        var seconds = milliseconds?.div(1000)
//        val hours = seconds?.div(3600)
//        val minutes = (seconds?.rem(3600))?.div(60)
//        seconds = seconds?.rem(60)
//
//        // Create a Formatter to format the time
//        val formatter: Formatter = Formatter(Locale.getDefault())
////        return formatter.format("%02d:%02d:%02d", hours, minutes, seconds).toString()
//        return formatter.format("%02d:%02d", minutes, seconds).toString()
//    }

    private fun formatDuration(milliseconds: Long): String {
        val totalSeconds = milliseconds / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60

        // Create a Formatter to format the time
        val formatter = Formatter(Locale.getDefault())
        return formatter.format("%02d:%02d", minutes, seconds).toString()
    }
}