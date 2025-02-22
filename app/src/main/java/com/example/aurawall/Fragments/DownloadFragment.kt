package com.example.aurawall.Fragments

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.aurawall.AdapterClass.CollectionAdapterClass
import com.example.aurawall.databinding.FragmentDownloadBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

class DownloadFragment : Fragment() {

    private lateinit var binding: FragmentDownloadBinding
    private lateinit var adView: AdView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDownloadBinding.inflate(inflater, container, false)

        // Initialize Mobile Ads SDK
        MobileAds.initialize(requireContext()) {}

        // Reference the AdView from XML
        adView = binding.adView

        // Load an ad
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)


        if (checkStoragePermission()) {
            loadWallpapers()
        } else {
            requestStoragePermission()
        }

        return binding.root
    }

    private fun checkStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                STORAGE_PERMISSION_CODE
            )
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_CODE
            )
        }
    }

    private fun loadWallpapers() {
        val imageList = arrayListOf<String>()

        val projection = arrayOf(MediaStore.Images.Media._ID)
        val selection = "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?"
        val selectionArgs = arrayOf("%Pictures/AuraWalls%")

        val cursor: Cursor? = requireContext().contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            "${MediaStore.Images.Media.DATE_ADDED} DESC"
        )

        cursor?.use {
            val columnIndex = it.getColumnIndex(MediaStore.Images.Media._ID)
            while (it.moveToNext()) {
                val imageId = it.getLong(columnIndex)
                val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon()
                    .appendPath(imageId.toString()).build()
                imageList.add(uri.toString()) // Use URI instead of file paths
            }
        }

        binding.collectionOfWallpaper.text = "${imageList.size} Wallpapers Downloaded"
        binding.rcvCollection.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.rcvCollection.adapter = CollectionAdapterClass(requireContext(), imageList)
    }

    companion object {
        private const val STORAGE_PERMISSION_CODE = 101
    }
}
