package com.example.tokobuku.core.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tokobuku.R
import com.example.tokobuku.core.data.model.rajaongkir.Costs
import com.example.tokobuku.util.Helper

class AdapterKurir(var data: ArrayList<Costs>, var kurir: String, var listener: Listeners):
    RecyclerView.Adapter<AdapterKurir.HolderData>() {
    class HolderData(view: View): RecyclerView.ViewHolder(view) {
        val tv_namakurir = view.findViewById<TextView>(R.id.nama_kurir)
        val tv_lamapengiriman = view.findViewById<TextView>(R.id.lama_pengiriman)
        val tv_hargakurir = view.findViewById<TextView>(R.id.harga_kurir)
        val tv_beratkurir = view.findViewById<TextView>(R.id.berat_barang)

        //        val layout = view.findViewById<CardView>(R.id.layout)
        val rb_pengiriman = view.findViewById<RadioButton>(R.id.rb_pengiriman)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderData {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_kurir, parent, false)
        return HolderData(view)
    }

    override fun onBindViewHolder(holder: HolderData, position: Int) {
        val a = data[position]

        holder.rb_pengiriman.isChecked = a.isActive
        holder.tv_namakurir.text = kurir + " " + a.service
        val cos = a.cost[0]
        holder.tv_lamapengiriman.text = cos.etd + " Hari kerja"
        holder.tv_hargakurir.text = Helper().formatRupiah(cos.value)
        holder.tv_beratkurir.text = "1 kg x " + Helper().formatRupiah(cos.value)
//        holder.tv_alamat.text =
//            a.alamat + ", " + a.kota + ", " + a.kecamatan + ", " + a.kodepos + ", (" + a.type + ")"
//
        holder.rb_pengiriman.setOnClickListener {
            a.isActive = true
            listener.onClicked(a, holder.adapterPosition)
        }
//        holder.layout.setOnClickListener {
//            a.isSelected = true
//            listener.onClicked(a)
//        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    interface Listeners {
        fun onClicked(data: Costs, index: Int)
    }
}