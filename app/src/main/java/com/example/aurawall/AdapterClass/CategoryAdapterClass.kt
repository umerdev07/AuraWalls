package com.example.aurawall.AdapterClass

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aurawall.CategoryActivity
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
        try {
            holder.names.text = listOfCategory[position].name
            Glide.with(holder.itemView.context) // Changed requireContext to holder.itemView.context
                .load(listOfCategory[position].link)
                .into(holder.image)

            holder.itemView.setOnClickListener {
                try {
                    val intent = Intent(holder.itemView.context, CategoryActivity::class.java)
                    intent.putExtra("name", listOfCategory[position].name)
                    intent.putExtra("uid", listOfCategory[position].id)
                    holder.itemView.context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(requireContext, "SomeThing Wrong!", Toast.LENGTH_SHORT).show()

                }
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext, "SomeThing Wrong! at admin side", Toast.LENGTH_SHORT).show()
        }
    }


    class catViewHolder(Itemview: View): RecyclerView.ViewHolder(Itemview) {
        val names:TextView = itemView.findViewById(R.id.catName)
        val image: ImageView = itemView.findViewById(R.id.catImage)
    }
}