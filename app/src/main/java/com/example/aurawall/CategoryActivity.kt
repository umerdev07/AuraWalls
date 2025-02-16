package com.example.aurawall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.aurawall.AdapterClass.CatImagesAdapterClass
import com.example.aurawall.Models.BomModel
import com.example.aurawall.Models.ColorModel
import com.example.aurawall.databinding.ActivityCategoryBinding
import com.google.firebase.firestore.FirebaseFirestore

class CategoryActivity : AppCompatActivity() {
    private val binding: ActivityCategoryBinding by lazy {
        ActivityCategoryBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.rcvCatItems.layoutManager

        val db = FirebaseFirestore.getInstance()
        val uid = intent.getStringExtra("uid")
        val catName = intent.getStringExtra("name")
        binding.catName.text = catName
        try {
            db.collection("Category").document(uid!!).collection("Wallpapers")
                .addSnapshotListener { value, error ->
                    val listOfWallpaer = arrayListOf<BomModel>()
                    val data = value?.toObjects(BomModel::class.java)
                    listOfWallpaer.addAll(data!!)

                    binding.countOfWallpaper.text = "${listOfWallpaer.size} Wallpapers Available"

                    binding.rcvCatItems.layoutManager =
                        StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                    binding.rcvCatItems.adapter = CatImagesAdapterClass(this, listOfWallpaer)
                }
        } catch (e: Exception) {
            Toast.makeText(this, "SoneThing Wrong! To fetch Wallpapers", Toast.LENGTH_SHORT).show()
        }
    }
}