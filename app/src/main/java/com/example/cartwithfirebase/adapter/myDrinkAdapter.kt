package com.example.cartwithfirebase.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cartwithfirebase.Models.DrinksModel
import com.example.cartwithfirebase.R

class myDrinkAdapter(private val context: Context,
                     private val list: List<DrinksModel>
                     ): RecyclerView.Adapter<myDrinkAdapter.myDrinkViewHolder>(){

    class myDrinkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

         var imageView: ImageView?=null
         var txtName: TextView?=null
         var txtPrice: TextView?=null

        init {
            imageView = itemView.findViewById(R.id.imageView) as ImageView
            txtName = itemView.findViewById(R.id.txtName) as TextView
            txtPrice = itemView.findViewById(R.id.txtPrice) as TextView

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myDrinkViewHolder {
        return myDrinkViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_drink_item,parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: myDrinkViewHolder, position: Int) {
        Glide.with(context).load(list[position].image).into(holder.imageView!!)

        holder.txtName!!.text = StringBuilder().append(list[position].name)
        holder.txtPrice!!.text = StringBuilder("$").append(list[position].price)

    }


}