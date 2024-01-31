package com.hienthai.tungkinhonline

import android.content.ContentResolver
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.util.MimeTypes
import com.hienthai.tungkinhonline.databinding.ActivityMainBinding
import org.koin.android.ext.android.inject
import java.io.File


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val prefs: AppPrefs by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val exoPlayer1 = initExo(0.5f)
//        val mediaItem1 = initMediaItem(R.raw.samhoi)
//        exoPlayer1.setMediaItem(mediaItem1)
//        exoPlayer1.prepare()


        val exoPlayer2 = initExo(1.0f)
        val mediaItem2 = initMediaItem(R.raw.gomo)

        val exoPlayer3 = initExo(1.0f)
        val mediaItem3 = initMediaItem(R.raw.chuong)

        binding.tvCount.text = prefs.count.toString()

        binding.imgGoMo.setSafeClickListener {
            prefs.count = ++prefs.count
            binding.tvCount.text = "${prefs.count}"
            startAudioAnimation(exoPlayer2, mediaItem2, it, Techniques.Pulse, 200)

        }

        binding.imgGoChuong.setSafeClickListener(interval = 10000) {
            startAudioAnimation(exoPlayer3, mediaItem3, it, Techniques.Swing, 100)
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun startAudioAnimation(
        exoPlayer: ExoPlayer,
        mediaItem: MediaItem,
        view: View,
        animation: Techniques,
        duration: Long
    ) {
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()

        YoYo.with(animation)
            .duration(duration)
            .playOn(view)
    }

    private fun initExo(duration: Float): ExoPlayer {
        val exoPlayer = ExoPlayer.Builder(applicationContext).build().apply {
            playWhenReady = true
            volume = duration
        }

        return exoPlayer
    }

    private fun initMediaItem(raw: Int): MediaItem {
        return MediaItem.Builder()
            .setUri(
                Uri.parse(
                    ContentResolver.SCHEME_ANDROID_RESOURCE +
                            File.pathSeparator +
                            File.separator +
                            File.separator +
                            applicationContext.packageName +
                            File.separator +
                            raw
                )
            )
            .setMimeType(MimeTypes.AUDIO_WAV)
            .build()
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