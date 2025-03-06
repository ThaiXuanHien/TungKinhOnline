package com.hienthai.tungkinhonline

import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
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
import com.hienthai.tungkinhonline.databinding.DialogTutorialBinding
import com.skydoves.powermenu.OnMenuItemClickListener
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import org.koin.android.ext.android.inject
import java.io.File


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
        binding.tvDailyPoint.text = prefs.dailyPoint.toString()

        binding.imgGoMo.setSafeClickListener {
            if (prefs.dailyPoint == 0) {
                Toast.makeText(this, getString(R.string.text_out_of_point), Toast.LENGTH_SHORT).show()
                return@setSafeClickListener
            }
            prefs.count = ++prefs.count
            prefs.dailyPoint -= 1
            binding.tvCount.text = "${prefs.count}"
            binding.tvDailyPoint.text = "${prefs.dailyPoint}"
            databaseReference.child(intent.getStringExtra("USER_ID") ?: "").child("count").setValue(prefs.count)
            startAudioAnimation(exoPlayer2, mediaItem2, it, Techniques.Pulse, 200)
        }

        binding.imgGoChuong.setSafeClickListener(interval = 5000) {
            startAudioAnimation(exoPlayer3, mediaItem3, it, Techniques.Swing, 100)
        }

        binding.imgMore.setSafeClickListener {
            powerMenu.showAsDropDown(it)
        }

        binding.imgWatchAd.setSafeClickListener {
            Toast.makeText(this, getString(R.string.text_feature_developing), Toast.LENGTH_SHORT).show()
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        if (prefs.notShowingToday.not()) {
            showTutorialDialog(this)
        }
    }

    private fun showTutorialDialog(context: Context) {
        val dialog = Dialog(context)

        val binding = DialogTutorialBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)

        binding.tvOk.setOnClickListener {
            dialog.dismiss()
        }

        binding.cbNotShowingToday.setOnCheckedChangeListener { _, isChecked ->
            prefs.notShowingToday = isChecked
        }

        dialog.show()
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
                    startActivity(Intent(this@MainActivity, SignInActivity::class.java))
                    prefs.clear()
                    finish()
                }, onNegativeButtonClick = {})
        }
    }
}