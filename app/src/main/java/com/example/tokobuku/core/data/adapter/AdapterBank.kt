package com.example.tokobuku.core.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tokobuku.R
import com.example.tokobuku.core.data.model.Bank
import kotlin.collections.ArrayList

class AdapterBank(var data: ArrayList<Bank>, var listener: Listeners) :
    RecyclerView.Adapter<AdapterBank.HolderData>() {
    class HolderData(view: View) : RecyclerView.ViewHolder(view) {
        val img_bank = view.findViewById<ImageView>(R.id.img_bank)
        val bank = view.findViewById<TextView>(R.id.bank)
        val layout = view.findViewById<RelativeLayout>(R.id.layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderData {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_bank, parent, false)
        return HolderData(view)
    }

    override fun onBindViewHolder(holder: HolderData, position: Int) {
        val a = data[position]

        holder.img_bank.setImageResource(a.image)
        holder.bank.text = a.nama

        holder.layout.setOnClickListener {
            listener.onCreate(a, holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    interface Listeners{
        fun onCreate(data:  Bank, index:Int)
    }
}