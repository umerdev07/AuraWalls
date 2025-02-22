package com.example.aurawall

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.aurawall.Fragments.DownloadFragment
import com.example.aurawall.Fragments.HomeFragment
import com.example.aurawall.databinding.ActivityMainBinding
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var interstitialAd: InterstitialAd? = null
    private var homeClickCount = 0
    private var downloadClickCount = 0
    private var quoteClickCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportActionBar?.hide()
        setContentView(binding.root)


        // ✅ Initialize Google Mobile Ads
        MobileAds.initialize(this) {}

        // ✅ Load Interstitial Ad
        loadInterstitialAd()

        // ✅ Default Fragment
        replaceFragment(HomeFragment())


        binding.icHome.setOnClickListener {
            homeClickCount++
            replaceFragment(HomeFragment())

            // Show Ad after 3 clicks
            if (homeClickCount == 3) {
                showInterstitialAd()
                homeClickCount = 0
            }
        }

        binding.icQuote.setOnClickListener {
            quoteClickCount++
            replaceFragment(QuoteGeneratorFragment())

            // Show Ad after 3 clicks
            if (quoteClickCount == 3) {
                showInterstitialAd()
                quoteClickCount = 0 // Reset count
            }
        }


        binding.icDownload.setOnClickListener {
            downloadClickCount++
            replaceFragment(DownloadFragment())

            // Show Ad after 3 clicks
            if (downloadClickCount == 3) {
                showInterstitialAd()
                downloadClickCount = 0 // Reset count
            }
        }
    }

    // ✅ Load Interstitial Ad
    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            "ca-app-pub-3940256099942544/1033173712",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                }
            }
        )
    }

    // ✅ Show Interstitial Ad
    private fun showInterstitialAd() {
        if (interstitialAd != null) {
            interstitialAd?.show(this)

            // Reload Ad After It's Closed
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    loadInterstitialAd()
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    interstitialAd = null
                    loadInterstitialAd()
                }
            }
        } else {
            loadInterstitialAd()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.replaceFragment, fragment)
            .commit()
    }
}
