package com.example.aurawall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils.replace
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.aurawall.Fragments.DownloadFragment
import com.example.aurawall.Fragments.HomeFragment
import com.example.aurawall.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private  val binding :ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater);
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()
        setContentView(binding.root)

        replaceFragment(HomeFragment())

        binding.icHome.setOnClickListener{
            replaceFragment(HomeFragment())
        }
        binding.icDownload.setOnClickListener{
            replaceFragment(DownloadFragment())
        }

    }

    fun replaceFragment(fragment:Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.replaceFragment, fragment)
        transaction.commit()
    }
}