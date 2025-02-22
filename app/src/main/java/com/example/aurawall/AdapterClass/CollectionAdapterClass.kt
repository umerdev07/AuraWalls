package com.example.aurawall.AdapterClass

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aurawall.FinalWallpaper
import com.example.aurawall.R
import com.makeramen.roundedimageview.RoundedImageView

class CollectionAdapterClass(val requireContext: Context, val listBestOfMonth: ArrayList<String>) :
    RecyclerView.Adapter<CollectionAdapterClass.bomViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CollectionAdapterClass.bomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.catwallpaper, parent, false)
        return bomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CollectionAdapterClass.bomViewHolder, position: Int) {
        try {
            Glide.with(requireContext).load(listBestOfMonth[position]).into(holder.image)
        }catch (e:Exception){
            Toast.makeText(requireContext, "SomeThing Wrong! at admin side", Toast.LENGTH_SHORT).show()
        }

    }

    override fun getItemCount(): Int {
        return listBestOfMonth.size
    }

    class bomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: RoundedImageView = itemView.findViewById(R.id.catImages)
    }
}