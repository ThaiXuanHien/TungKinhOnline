package com.hienthai.tungkinhonline

import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.hienthai.tungkinhonline.databinding.ActivityMainBinding
import com.hienthai.tungkinhonline.databinding.DialogTutorialBinding
import com.skydoves.powermenu.OnMenuItemClickListener
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.io.File


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val prefs: AppPrefs by inject()

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    private lateinit var powerMenu: PowerMenu

    private var rewardedAd: RewardedAd? = null
    private val adUnitId = "ca-app-pub-9207898971714644/1484763633"
    private val backgroundScope = CoroutineScope(Dispatchers.IO)
    private var player: ExoPlayer? = null
    private var lastUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        backgroundScope.launch {
            MobileAds.initialize(this@MainActivity) {}
        }

        loadRewardedAd()

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
                Toast.makeText(this, getString(R.string.text_out_of_point), Toast.LENGTH_SHORT)
                    .show()
                return@setSafeClickListener
            }
            prefs.count = ++prefs.count
            prefs.dailyPoint -= 1
            binding.tvCount.text = "${prefs.count}"
            binding.tvDailyPoint.text = "${prefs.dailyPoint}"
            databaseReference.child(intent.getStringExtra("USER_ID") ?: "").child("count")
                .setValue(prefs.count)
            startAudioAnimation(exoPlayer2, mediaItem2, it, Techniques.Pulse, 200)
        }

        binding.imgGoChuong.setSafeClickListener(interval = 5000) {
            startAudioAnimation(exoPlayer3, mediaItem3, it, Techniques.Swing, 100)
        }

        binding.imgMore.setSafeClickListener {
            powerMenu.showAsDropDown(it)
        }

        binding.imgWatchAd.setSafeClickListener {
            showRewardedAd()
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        if (prefs.notShowingToday.not()) {
            showTutorialDialog(this)
        }
    }

    private fun loadRewardedAd() {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(this, adUnitId, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.text_load_ad_fail),
                    Toast.LENGTH_SHORT
                ).show()
                rewardedAd = null

            }

            override fun onAdLoaded(ad: RewardedAd) {
                rewardedAd = ad
                rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdClicked() {
                        // Called when a click is recorded for an ad.
                        prefs.count += 200
                        binding.tvCount.text = "${prefs.count}"
                        databaseReference.child(intent.getStringExtra("USER_ID") ?: "")
                            .child("count").setValue(prefs.count)
                    }

                    override fun onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.
                        // Set the ad reference to null so you don't show the ad a second time.
                        rewardedAd = null
                        loadRewardedAd()

                    }

                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        super.onAdFailedToShowFullScreenContent(p0)
                        rewardedAd = null
                    }

                    override fun onAdImpression() {
                        // Called when an impression is recorded for an ad.
                    }

                    override fun onAdShowedFullScreenContent() {
                        // Called when ad is shown.

                    }
                }
                Log.d("AdMob", "Rewarded Ad Loaded.")
            }
        })
    }

    private fun showRewardedAd() {
        rewardedAd?.let { ad ->
            ad.show(this) { _ ->
                prefs.count += 100
                binding.tvCount.text = "${prefs.count}"
                databaseReference.child(intent.getStringExtra("USER_ID") ?: "").child("count")
                    .setValue(prefs.count)
            }
        } ?: run {
            Toast.makeText(this, getString(R.string.text_no_ads), Toast.LENGTH_SHORT).show()
            loadRewardedAd()
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
            .addItem(PowerMenuItem(getString(R.string.text_select_music)))
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
            when (position) {
                0 -> {
                    startActivity(Intent(this@MainActivity, RankActivity::class.java))
                }

                1 -> {
                    openMusicPicker()
                }

                2 -> {
                    startActivity(Intent(this@MainActivity, SignInActivity::class.java))
                    prefs.clear()
                    finish()
                }
            }
            powerMenu.dismiss()

        }

    private fun openMusicPicker() {
        pickAudioLauncher.launch("audio/*")
    }

    private val pickAudioLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { result ->
            result?.let { playMusic(it) }
        }

    private fun playMusic(uri: Uri) {
        lastUri = uri
        player?.release()
        player = null

        player = ExoPlayer.Builder(this)
            .build()
            .apply {
                val mediaItem = MediaItem.fromUri(uri)
                setMediaItem(mediaItem)
                playWhenReady = true
                prepare()
            }
    }

    private fun startAudioAnimation(
        exoPlayer: ExoPlayer,
        mediaItem: MediaItem,
        view: View,
        animation: Techniques,
        duration: Long,
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
                (ContentResolver.SCHEME_ANDROID_RESOURCE +
                        File.pathSeparator +
                        File.separator +
                        File.separator +
                        applicationContext.packageName +
                        File.separator +
                        raw).toUri()
            )
            .setMimeType(MimeTypes.AUDIO_WAV)
            .build()
    }

    override fun onStart() {
        super.onStart()
        lastUri?.let {
            if (player == null) {
                playMusic(it)
            } else {
                player?.play()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
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