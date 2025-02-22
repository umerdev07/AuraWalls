package com.example.aurawall.AdapterClass

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aurawall.Models.ColorModel
import com.example.aurawall.R
import android.widget.ImageView
import androidx.cardview.widget.CardView
import com.example.aurawall.FinalWallpaper

class ColorAdapterClass(
    private val context: Context,
    private val listColorTone: ArrayList<ColorModel>
) : RecyclerView.Adapter<ColorAdapterClass.ColorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.color_tone_card, parent, false)
        return ColorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val color = listColorTone[position].color

        if (!color.isNullOrEmpty()) {
            try {
                holder.colorView.setBackgroundColor(Color.parseColor(color))
            } catch (e: IllegalArgumentException) {
                holder.colorView.setBackgroundColor(Color.GRAY) // Set a default color
                e.printStackTrace()
            }
        } else {
            holder.colorView.setBackgroundColor(Color.GRAY) // Default color if empty
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, FinalWallpaper::class.java)
            intent.putExtra("link", listColorTone[position].link)
            context.startActivity(intent)
        }
    }


    override fun getItemCount(): Int {
        return listColorTone.size
    }

    class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val colorView: CardView = itemView.findViewById(R.id.item_card)
    }
}
