package com.example.aurawall.AdapterClass

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.aurawall.FinalWallpaper
import com.example.aurawall.Models.BomModel
import com.example.aurawall.R
import com.makeramen.roundedimageview.RoundedImageView

class CatImagesAdapterClass(val requireContext: Context, val listBestOfMonth: ArrayList<BomModel>) :
    RecyclerView.Adapter<CatImagesAdapterClass.bomViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CatImagesAdapterClass.bomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.catwallpaper, parent, false)
        return bomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CatImagesAdapterClass.bomViewHolder, position: Int) {
        try {
            Glide.with(holder.itemView.context)
                .load(listBestOfMonth[position].link)
                .into(holder.image)

            holder.itemView.setOnClickListener {
                try {
                    val intent = Intent(holder.itemView.context, FinalWallpaper::class.java)
                    intent.putExtra("link", listBestOfMonth[position].link)
                    holder.itemView.context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(requireContext, "SomeThing Wrong!", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
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