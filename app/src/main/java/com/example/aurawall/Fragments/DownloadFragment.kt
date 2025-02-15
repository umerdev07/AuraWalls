package com.example.aurawall.Fragments

import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.aurawall.AdapterClass.CollectionAdapterClass
import com.example.aurawall.databinding.FragmentDownloadBinding
import java.io.File

class DownloadFragment : Fragment() {
    lateinit var binding: FragmentDownloadBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDownloadBinding.inflate(layoutInflater, container, false)

        val allFiles: ArrayList<File> = ArrayList()
        val imageList = arrayListOf<String>()

        //Permission


        //Denied


        //Allowed


        val targetPath = Environment.getExternalStorageDirectory().absolutePath + "/Pictures/AuraWalls"
        val targetFile = File(targetPath)

        targetFile.listFiles()?.let { allFiles.addAll(it) }

        for (data in allFiles) {
            imageList.add(data.absolutePath)
        }

        binding.collectionOfWallpaper.text = "${imageList.size} Wallpapers Downloaded"

        binding.rcvCollection.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.rcvCollection.adapter=CollectionAdapterClass(requireContext(), imageList)

        return binding.root
    }
}
