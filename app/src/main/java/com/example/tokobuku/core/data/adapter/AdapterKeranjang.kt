package com.example.tokobuku.core.data.adapter

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.tokobuku.R
import com.example.tokobuku.core.data.model.Produk
import com.example.tokobuku.core.data.room.MyDatabase
import com.example.tokobuku.util.Helper
import com.example.tokobuku.util.Util
import com.labters.lottiealertdialoglibrary.ClickListener
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class AdapterKeranjang(
    var activity: Activity,
    var data: ArrayList<Produk>,
    var listener: Listeners
) : RecyclerView.Adapter<AdapterKeranjang.HolderData>() {
    lateinit var context: Context

    class HolderData(view: View) : RecyclerView.ViewHolder(view) {
        val tv_nama = view.findViewById<TextView>(R.id.nama_produk)
        val tv_harga = view.findViewById<TextView>(R.id.total)
        val tv_gambar = view.findViewById<ImageView>(R.id.image)
        val layout = view.findViewById<CardView>(R.id.layout)
        val btn_tambah = view.findViewById<ImageView>(R.id.btn_add)
        val btn_kurang = view.findViewById<ImageView>(R.id.btn_min)
        val btn_delete = view.findViewById<ImageView>(R.id.btn_delete)
        val cekbok = view.findViewById<CheckBox>(R.id.cek)
        val txt_angka = view.findViewById<TextView>(R.id.txt_angka)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderData {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_keranjang, parent, false)
        return HolderData(view)
    }

    override fun onBindViewHolder(holder: HolderData, position: Int) {
        val produk = data[position]
        val harga = Integer.valueOf(produk.harga)

        holder.tv_nama.text = produk.nama_produk
        holder.tv_harga.text = Helper().formatRupiah(harga * produk.jumlah)
            .format(Integer.valueOf(produk.harga))
        var jumlah = produk.jumlah
        holder.txt_angka.text = jumlah.toString()

        val imageUrl = Util.produkUrl + data[position].image
        Picasso.get()
            .load(imageUrl)
            .placeholder(R.drawable.ic_image)
            .error(R.drawable.ic_image)
            .into(holder.tv_gambar)

        holder.btn_tambah.setOnClickListener {
            jumlah++
            produk.jumlah = jumlah
            update(produk)
            holder.txt_angka.text = jumlah.toString()
            holder.tv_harga.text = Helper().formatRupiah((harga * jumlah).toString())
        }
        holder.btn_kurang.setOnClickListener {
            if (jumlah <= 1) return@setOnClickListener

            jumlah--
            produk.jumlah = jumlah
            update(produk)
            holder.txt_angka.text = jumlah.toString()
            holder.tv_harga.text = Helper().formatRupiah((harga * jumlah).toString())
        }
        holder.btn_delete.setOnClickListener {
            val alertDialog: LottieAlertDialog =
                LottieAlertDialog.Builder(activity, DialogTypes.TYPE_WARNING)
                    .setTitle("Yakin ingin menghapus?")
                    .setDescription("Produk yang dipilih akan terhapus secara permanen.")
                    .setPositiveText("Iya")
                    .setPositiveTextColor(Color.parseColor("#ffeaea"))
                    .setPositiveButtonColor(Color.parseColor("#f44242"))
                    .setPositiveListener(object : ClickListener {
                        override fun onClick(dialog: LottieAlertDialog) {
                            delete(produk)
                            listener.onDelete(position)
                            dialog.dismiss()
                        }
                    })
                    .setNegativeText("Tidak")
                    .setNegativeTextColor(Color.parseColor("#ffeaea"))
                    .setNegativeButtonColor(Color.parseColor("#EDEDED"))
                    .setNegativeListener(object : ClickListener {
                        override fun onClick(dialog: LottieAlertDialog) {
                            dialog.dismiss()
                        }
                    })
                    .build()
            alertDialog.show()
        }
        holder.cekbok.isChecked = produk.selected
        holder.cekbok.setOnCheckedChangeListener { buttonView, isChecked ->
            produk.selected = isChecked
            update(produk)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    interface Listeners {
        fun onUpdate()
        fun onDelete(position: Int)
    }

    private fun update(produkData: Produk) {
        val myDb = MyDatabase.getInstance(activity)
        CompositeDisposable().add(Observable.fromCallable {
            myDb!!.daoKeranjang().update(produkData)
        }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                listener.onUpdate()
            })

    }

    private fun delete(produkData: Produk) {
        val myDb = MyDatabase.getInstance(activity)
        CompositeDisposable().add(Observable.fromCallable {
            myDb!!.daoKeranjang().delete(produkData)
        }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
            })

    }

}