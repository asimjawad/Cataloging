package com.example.cataloging.adapters

import android.content.Intent
import android.icu.number.NumberFormatter.with
import android.icu.number.NumberRangeFormatter.with
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cataloging.EditProductActivity
import com.example.cataloging.R
import com.example.cataloging.datamodels.ProductForEdit
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class ProductAdapter(private var products: List<ProductForEdit>): RecyclerView.Adapter<ProductAdapter.ViewHolder>() {



    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val itemImg : ImageView = itemView.findViewById(R.id.product_img_img)
        val itemName : TextView = itemView.findViewById(R.id.name_txt)
        val itemQuantity : TextView = itemView.findViewById(R.id.quanitty_txt)
        val itemPrice : TextView = itemView.findViewById(R.id.price_txt)

        init {
            itemView.setOnClickListener { v: View ->
                val position : Int =adapterPosition
                val intent = Intent(v.context,EditProductActivity::class.java)
                val context = v.context
                intent.putExtra("Value",products[position])
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.product,parent,false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemName.text = products[position].name
        holder.itemQuantity.text = products[position].quantity.toString()
        holder.itemPrice.text = products[position].price.toString()
        val url = products[position].image
        Picasso.get().load(url).into(holder.itemImg)
    }

    override fun getItemCount(): Int {
       return products.size
    }
}