package com.example.aurawall

import android.Manifest
import android.app.WallpaperManager
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.aurawall.databinding.ActivityFinalWallpaperBinding
import kotlinx.coroutines.*
import java.io.IOException
import java.net.URL
import java.util.Random

class FinalWallpaper : AppCompatActivity() {
    private val binding: ActivityFinalWallpaperBinding by lazy {
        ActivityFinalWallpaperBinding.inflate(layoutInflater)
    }

    private val STORAGE_PERMISSION_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportActionBar?.hide()
        setContentView(binding.root)

        val url = intent.getStringExtra("link") ?: return
        val urlImage = URL(url)

        Glide.with(this).load(url).into(binding.fullBg)

        // Download & Save Image
        binding.downloadBtn.setOnClickListener {
            if (checkStoragePermission()) {
                downloadAndSaveImage(urlImage)
            } else {
                requestStoragePermission()
            }
        }

        // Apply Wallpaper
        binding.applyBtn.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val bitmap = urlImage.toBitmap()
                withContext(Dispatchers.Main) {
                    if (bitmap != null) {
                        applyWallpaper(bitmap)
                    } else {
                        Toast.makeText(this@FinalWallpaper, "Failed to load image", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun downloadAndSaveImage(url: URL) {
        CoroutineScope(Dispatchers.IO).launch {
            val bitmap = url.toBitmap()
            withContext(Dispatchers.Main) {
                if (bitmap != null) {
                    saveImage(bitmap)
                } else {
                    Toast.makeText(this@FinalWallpaper, "Failed to load image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun applyWallpaper(bitmap: Bitmap) {
        val wallpaperManager = WallpaperManager.getInstance(applicationContext)
        wallpaperManager.setBitmap(bitmap)
        Toast.makeText(applicationContext, "Wallpaper is set", Toast.LENGTH_SHORT).show()
    }

    private fun saveImage(image: Bitmap) {
        val random1 = Random().nextInt(520985)
        val random2 = Random().nextInt(952663)
        val name = "Aura-$random1$random2.jpg"

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/AuraWalls")
        }

        try {
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)?.let { uri ->
                contentResolver.openOutputStream(uri)?.use { outputStream ->
                    image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                }
                Toast.makeText(this, "Image Saved", Toast.LENGTH_SHORT).show()
            } ?: throw IOException("Failed to create new MediaStore record")
        } catch (e: IOException) {
            Toast.makeText(this, "Image Not Saved", Toast.LENGTH_SHORT).show()
        }
    }

    // ✅ Checks Storage Permission
    private fun checkStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            true // No permission needed for Android 13+
        } else {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    // ✅ Requests Storage Permission
    private fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_CODE
            )
        }
    }

    // ✅ Handles Permission Result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted. Saving Image...", Toast.LENGTH_SHORT).show()
                val url = intent.getStringExtra("link") ?: return
                downloadAndSaveImage(URL(url))
            } else {
                Toast.makeText(this, "Permission Denied. Cannot Save Image.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun URL.toBitmap(): Bitmap? {
        return try {
            BitmapFactory.decodeStream(openStream())
        } catch (e: IOException) {
            null
        }
    }
}
