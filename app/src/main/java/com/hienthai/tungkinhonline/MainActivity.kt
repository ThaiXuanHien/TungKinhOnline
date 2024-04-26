package com.hienthai.tungkinhonline

import android.content.ContentResolver
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.util.MimeTypes
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.hienthai.tungkinhonline.databinding.ActivityMainBinding
import com.skydoves.powermenu.OnMenuItemClickListener
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import org.koin.android.ext.android.inject
import java.io.File
import java.util.Locale
import kotlin.math.log


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val prefs: AppPrefs by inject()

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    private lateinit var powerMenu: PowerMenu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initMenu()

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("users")

        val exoPlayer2 = initExo(1.0f)
        val mediaItem2 = initMediaItem(R.raw.gomo)

        val exoPlayer3 = initExo(1.0f)
        val mediaItem3 = initMediaItem(R.raw.chuong)

        binding.tvCount.text = prefs.count.toString()

        binding.imgGoMo.setSafeClickListener {
            prefs.count = ++prefs.count
            binding.tvCount.text = "${prefs.count}"
            databaseReference.child(intent.getStringExtra("USER_ID") ?: "").child("count").setValue(prefs.count)
            startAudioAnimation(exoPlayer2, mediaItem2, it, Techniques.Pulse, 200)

        }

        binding.imgGoChuong.setSafeClickListener(interval = 10000) {
            startAudioAnimation(exoPlayer3, mediaItem3, it, Techniques.Swing, 100)
        }

        binding.imgMore.setSafeClickListener {
            powerMenu.showAsDropDown(it)
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun initMenu() {
        powerMenu = PowerMenu.Builder(this)
            .addItem(PowerMenuItem(getString(R.string.text_rank)))
            .addItem(PowerMenuItem(getString(R.string.text_log_out)))
            .setMenuRadius(10f)
            .setMenuShadow(10f)
            .setTextGravity(Gravity.CENTER)
            .setTextColor(Color.BLACK)
            .setSelectedTextColor(Color.RED)
            .setMenuColor(Color.WHITE)
            .setOnMenuItemClickListener(onMenuItemClickListener)
            .build()
    }

    private val onMenuItemClickListener: OnMenuItemClickListener<PowerMenuItem> =
        OnMenuItemClickListener<PowerMenuItem> { position, item ->
            powerMenu.selectedPosition = position
            when(position) {
                0 -> {
                    startActivity(Intent(this@MainActivity, RankActivity::class.java))
                }
                1 -> {
                    startActivity(Intent(this@MainActivity, SignInActivity::class.java))
                    prefs.clear()
                    finish()
                }
            }
            powerMenu.dismiss()

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
                msg = getString(R.string.text_question_out),
                positiveButton = getString(R.string.text_down_the_mountain),
                negativeButton = getString(R.string.text_continue_practicing),
                onPositiveButtonClick = {
                    finish()
                }, onNegativeButtonClick = {})
        }
    }
}