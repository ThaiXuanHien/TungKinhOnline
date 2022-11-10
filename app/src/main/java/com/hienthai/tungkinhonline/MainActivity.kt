package com.hienthai.tungkinhonline

import android.content.ContentResolver
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.util.MimeTypes
import com.hienthai.tungkinhonline.databinding.ActivityMainBinding
import java.io.File


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val exoPlayer1 = ExoPlayer.Builder(applicationContext).build().apply {
            playWhenReady = true
            volume = 0.3f
        }
        val mediaItem1: MediaItem = MediaItem.Builder()
            .setUri(
                Uri.parse(
                    ContentResolver.SCHEME_ANDROID_RESOURCE +
                            File.pathSeparator +
                            File.separator +
                            File.separator +
                            applicationContext.packageName +
                            File.separator +
                            R.raw.samhoi
                )
            )
            .setMimeType(MimeTypes.AUDIO_WAV)
            .build()

        exoPlayer1.setMediaItem(mediaItem1)
        exoPlayer1.prepare()


        val exoPlayer2 = ExoPlayer.Builder(applicationContext).build().apply {
            playWhenReady = true
            volume = 1.0f
        }

        val mediaItem2: MediaItem = MediaItem.Builder()
            .setUri(
                Uri.parse(
                    ContentResolver.SCHEME_ANDROID_RESOURCE +
                            File.pathSeparator +
                            File.separator +
                            File.separator +
                            applicationContext.packageName +
                            File.separator +
                            R.raw.gomo
                )
            )
            .setMimeType(MimeTypes.AUDIO_WAV)
            .build()

        val exoPlayer3 = ExoPlayer.Builder(applicationContext).build().apply {
            playWhenReady = true
            volume = 0.3f
        }
        val mediaItem3: MediaItem = MediaItem.Builder()
            .setUri(
                Uri.parse(
                    ContentResolver.SCHEME_ANDROID_RESOURCE +
                            File.pathSeparator +
                            File.separator +
                            File.separator +
                            applicationContext.packageName +
                            File.separator +
                            R.raw.chuong
                )
            )
            .setMimeType(MimeTypes.AUDIO_WAV)
            .build()


        var i = 0
        binding.imgGoMo.setOnClickListener {

            exoPlayer2.setMediaItem(mediaItem2)
            exoPlayer2.prepare()

            binding.tvCount.text = "${++i}"

            if (i == 1000) {
                Toast.makeText(this, "Chúc mừng bạn đã chính quả", Toast.LENGTH_SHORT).show()
            }

            YoYo.with(Techniques.Pulse)
                .duration(200)
                .playOn(it)
        }

        binding.imgGoChuong.setOnClickListener {

            exoPlayer3.setMediaItem(mediaItem3)
            exoPlayer3.prepare()

            YoYo.with(Techniques.Swing)
                .duration(100)
                .playOn(it)
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            showAlertDialog(
                msg = "Bạn chưa Chính Quả, bạn có muốn tiếp tục Tu ko?",
                positiveButton = "Xuống núi",
                negativeButton = "Tu tiếp",
                onPositiveButtonClick = {
                    finish()
                }, onNegativeButtonClick = {})
        }
    }
}