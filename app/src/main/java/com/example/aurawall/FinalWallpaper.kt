package com.example.aurawall

import android.app.WallpaperManager
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.aurawall.databinding.ActivityFinalWallpaperBinding
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.net.URL
import java.util.Random

class FinalWallpaper : AppCompatActivity() {
    private val binding: ActivityFinalWallpaperBinding by lazy{
        ActivityFinalWallpaperBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()
        setContentView(binding.root)
        val url = intent.getStringExtra("link")

        val urlImage = URL(url)

        Glide.with(this).load(url).into( binding.fullBg);

        binding.downloadBtn.setOnClickListener{
            val result: kotlinx.coroutines.Deferred<Bitmap?> = GlobalScope.async {
                urlImage.toBitmap()
            }

            GlobalScope.launch (Dispatchers.Main) {
                saveImage(result.await())
            }
        }

        binding.applyBtn.setOnClickListener{

            val result: kotlinx.coroutines.Deferred<Bitmap?> = GlobalScope.async {
                urlImage.toBitmap()
            }

            GlobalScope.launch (Dispatchers.Main) {
                val wallpaperManager = WallpaperManager.getInstance(applicationContext)
                wallpaperManager.setBitmap(result.await())
                Toast.makeText(applicationContext, "Wallpaper is set", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveImage(image: Bitmap?) {
        val random1 = Random().nextInt(520985)
        val random2 = Random().nextInt(952663)
        val name = "Aura-$random1$random2"

        val resolver = contentResolver
        val contentValues = ContentValues()

        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "$name.jpg")
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
        contentValues.put(
            MediaStore.MediaColumns.RELATIVE_PATH,
            Environment.DIRECTORY_PICTURES + File.separator + "AuraWalls"
        )

        try {
            val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            imageUri?.let {
                resolver.openOutputStream(it)?.use { outputStream ->
                    image?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                }
                Toast.makeText(this, "Image Saved", Toast.LENGTH_SHORT).show()
            } ?: throw Exception("Failed to create new MediaStore record")
        } catch (e: Exception) {
            Toast.makeText(this, "Image Not Saved", Toast.LENGTH_SHORT).show()
        }
    }


    fun URL.toBitmap(): Bitmap? {
        return try {
            BitmapFactory.decodeStream(openStream())
        }
        catch (e: IOException){
            null
        }
    }
}