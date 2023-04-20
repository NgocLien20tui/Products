package com.example.products.adapter

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.products.ProductModel
import com.example.products.R

class ProductAdapter(private val ds:ArrayList<ProductModel>): RecyclerView.Adapter<ProductAdapter.ViewHolder>()  {
    private lateinit var mListener: OnItemClickListener
    interface OnItemClickListener{
        fun onItemCLick(position: Int)
    }

    fun setOnItemClickListener(clickListener: OnItemClickListener){
        mListener = clickListener
    }

    class ViewHolder(itemView: View, clickListener: OnItemClickListener): RecyclerView.ViewHolder(itemView){
        init {
            itemView.setOnClickListener{
                clickListener.onItemCLick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.product_list_items, parent, false)
        return ViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.itemView.apply {
            val txtView = findViewById<TextView>(R.id.tvEmpName)
            val imgView = findViewById<ImageView>(R.id.imgProductList)
            txtView.text = ds[position].PrdName
            val imageBytes = Base64.decode(ds[position].PrdImg, Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            Glide.with(this).load(decodedImage).diskCacheStrategy(
                DiskCacheStrategy.ALL).circleCrop().into(imgView)
        }
    }

    override fun getItemCount(): Int {
        return ds.size
    }
}