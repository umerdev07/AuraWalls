package com.example.aurawall.AdapterClass

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aurawall.CategoryActivity
import com.example.aurawall.FinalWallpaper
import com.example.aurawall.Models.CatModel
import com.example.aurawall.R

class CategoryAdapterClass(val requireContext: Context, val listOfCategory: ArrayList<CatModel>):RecyclerView.Adapter<CategoryAdapterClass.catViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): catViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_card, parent, false)
        return catViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listOfCategory.size
    }

    override fun onBindViewHolder(holder: catViewHolder, position: Int) {
        holder.names.text = listOfCategory[position].name
        Glide.with(requireContext).load(listOfCategory[position].link).into( holder.image);

            holder.itemView.setOnClickListener {
                val intent = Intent(requireContext, CategoryActivity::class.java)
                intent.putExtra("name", listOfCategory[position].name)
                intent.putExtra("uid", listOfCategory[position].id)
                requireContext.startActivity(intent)
            }
        }

    class catViewHolder(Itemview: View): RecyclerView.ViewHolder(Itemview) {
        val names:TextView = itemView.findViewById(R.id.catName)
        val image: ImageView = itemView.findViewById(R.id.catImage)
    }
}