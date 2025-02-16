package com.example.aurawall.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aurawall.AdapterClass.BomAdapterClass
import com.example.aurawall.AdapterClass.CategoryAdapterClass
import com.example.aurawall.AdapterClass.ColorAdapterClass
import com.example.aurawall.Models.BomModel
import com.example.aurawall.Models.CatModel
import com.example.aurawall.Models.ColorModel
import com.example.aurawall.databinding.FragmentHomeBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var db: FirebaseFirestore
    private lateinit var adView: AdView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)


        // Initialize Mobile Ads SDK
        MobileAds.initialize(requireContext()) {}

        // Reference the AdView from XML
        adView = binding.adView

        // Load an ad
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)




        db = FirebaseFirestore.getInstance()
        try {


            db.collection("BestOfTheMonth").addSnapshotListener { value, error ->
                val listBestOfMonth = arrayListOf<BomModel>()
                val data = value?.toObjects(BomModel::class.java)
                listBestOfMonth.addAll(data!!)


                binding.rcvBom.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                binding.rcvBom.adapter
                binding.rcvBom.adapter = BomAdapterClass(requireContext(), listBestOfMonth)
            }
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "SomeThing Wrong! To fetch Best of Month Wallpapers",
                Toast.LENGTH_SHORT
            ).show()
        }


     try {
         db.collection("ColorTone").addSnapshotListener { value, error ->
             val listColorTone = arrayListOf<ColorModel>()
             val data = value?.toObjects(ColorModel::class.java)
             listColorTone.addAll(data!!)


             binding.rcvtct.layoutManager =
                 LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
             binding.rcvtct.adapter
             binding.rcvtct.adapter = ColorAdapterClass(requireContext(), listColorTone)
         }
     }catch (e: Exception) {
         Toast.makeText(
             requireContext(),
             "SomeThing Wrong! To fetch Color of tone Wallpapers",
             Toast.LENGTH_SHORT
         ).show()
     }
        try {
         db.collection("Category").addSnapshotListener { value, error ->
             val listOfCategory = arrayListOf<CatModel>()
             val data = value?.toObjects(CatModel::class.java)
             listOfCategory.addAll(data!!)

             binding.rcvcat.layoutManager = GridLayoutManager(requireContext(), 2)

             binding.rcvcat.adapter
             binding.rcvcat.adapter = CategoryAdapterClass(requireContext(), listOfCategory)
         }

     }catch (e:Exception){
            Toast.makeText(requireContext(), "SomeThing Wrong! to fetch Categories", Toast.LENGTH_SHORT).show()
     }
        return binding.root
    }
}