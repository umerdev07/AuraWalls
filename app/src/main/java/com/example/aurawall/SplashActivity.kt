package com.example.aurawall

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import com.example.aurawall.databinding.ActivityMainBinding
import com.example.aurawall.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity: AppCompatActivity() {
    private  val binding :ActivitySplashBinding by lazy {
        ActivitySplashBinding.inflate(layoutInflater);
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()
        setContentView(binding.root)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000)
    }
}