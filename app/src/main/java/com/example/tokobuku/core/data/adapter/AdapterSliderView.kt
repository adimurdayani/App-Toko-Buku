package com.example.tokobuku.core.data.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.tokobuku.R
import com.smarteist.autoimageslider.SliderViewAdapter
import com.squareup.picasso.Picasso

class AdapterSliderView() :
    SliderViewAdapter<AdapterSliderView.HolderData>() {
    private var mSliderItems = ArrayList<Int>()

    fun renewItems(sliderItems: ArrayList<Int>) {
        mSliderItems = sliderItems
        notifyDataSetChanged()
    }

    fun addItem(sliderItem: Int) {
        mSliderItems.add(sliderItem)
        notifyDataSetChanged()
    }

    class HolderData(itemView: View) : SliderViewAdapter.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.imageSlider)
    }

    override fun getCount(): Int {
        return mSliderItems.size
    }

    @SuppressLint("InflateParams")
    override fun onCreateViewHolder(parent: ViewGroup): HolderData {
        val inflate: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_carouser_banner, null)
        return HolderData(inflate)
    }

    override fun onBindViewHolder(viewHolder: HolderData, position: Int) {
        Picasso.get().load(mSliderItems[position]).fit().into(viewHolder.imageView)
    }
}