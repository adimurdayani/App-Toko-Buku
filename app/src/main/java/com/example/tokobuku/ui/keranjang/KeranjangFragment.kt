package com.example.tokobuku.ui.keranjang

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tokobuku.PengirimanActivity
import com.example.tokobuku.R
import com.example.tokobuku.core.data.adapter.AdapterKeranjang
import com.example.tokobuku.core.data.adapter.AdapterSliderView
import com.example.tokobuku.core.data.model.Produk
import com.example.tokobuku.core.data.room.MyDatabase
import com.example.tokobuku.ui.auth.LoginActivity
import com.example.tokobuku.util.Helper
import com.example.tokobuku.util.SharedPref
import com.facebook.shimmer.ShimmerFrameLayout
import com.labters.lottiealertdialoglibrary.ClickListener
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog
import com.smarteist.autoimageslider.SliderView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class KeranjangFragment : Fragment() {
    lateinit var btn_delete: ImageView
    lateinit var btn_beli: LinearLayout
    lateinit var total: TextView
    lateinit var rc_data: RecyclerView
    lateinit var cekall: CheckBox
    lateinit var myDb: MyDatabase
    lateinit var s: SharedPref
    lateinit var adapter: AdapterKeranjang
    var listProduk = ArrayList<Produk>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_keranjang, container, false)
        setinit(view)
        myDb = MyDatabase.getInstance(requireActivity())!!
        s = SharedPref(requireActivity())
        setButton()
        return view
    }

    private fun displayProduk() {
        listProduk = myDb.daoKeranjang().getAll() as ArrayList

        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        adapter =
            AdapterKeranjang(requireActivity(), listProduk, object : AdapterKeranjang.Listeners {
                override fun onUpdate() {
                    hitungTotal()
                }

                override fun onDelete(position: Int) {
                    listProduk.removeAt(position)
                    adapter.notifyDataSetChanged()
                    hitungTotal()
                }

            })
        rc_data.adapter = adapter
        rc_data.layoutManager = layoutManager
    }

    var totalHarga = 0
    fun hitungTotal() {
        val listProduk = myDb.daoKeranjang().getAll() as ArrayList
        totalHarga = 0
        var isSelectedAll = true
        for (produk in listProduk) {
            if (produk.selected) {
                val harga = Integer.valueOf(produk.harga)
                totalHarga += (harga * produk.jumlah)
            } else {
                isSelectedAll = false
            }
        }
        cekall.isChecked = isSelectedAll
        total.text = Helper().formatRupiah(totalHarga)
    }

    private fun setButton() {
        btn_delete.setOnClickListener {
            hapus()
        }
        btn_beli.setOnClickListener {

            if (s.getStatusLogin()) {
                var isThereProduk = false
                for (p in listProduk) {
                    if (p.selected) isThereProduk = true
                }

                if (isThereProduk) {
                    val intent = Intent(activity, PengirimanActivity::class.java)
                    intent.putExtra("extra", "" + totalHarga)
                    startActivity(intent)
                } else {
                    setError("Tidak ada produk yang pilih")
                }
            } else {
                startActivity(Intent(requireActivity(), LoginActivity::class.java))
            }

        }
        cekall.setOnClickListener {
            for (i in listProduk.indices) {
                val produk = listProduk[i]
                produk.selected = cekall.isChecked
                listProduk[i] = produk
            }
            adapter.notifyDataSetChanged()
        }
    }

    private fun delete(produkData: ArrayList<Produk>) {
        CompositeDisposable().add(Observable.fromCallable {
            myDb.daoKeranjang().delete(produkData)
        }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                listProduk.clear()
                listProduk.addAll(myDb.daoKeranjang().getAll() as ArrayList)
                adapter.notifyDataSetChanged()
            })

    }

    private fun setinit(view: View) {
        btn_delete = view.findViewById(R.id.btn_delete)
        btn_beli = view.findViewById(R.id.btn_beli2)
        total = view.findViewById(R.id.total_harga)
        rc_data = view.findViewById(R.id.rc_data)
        cekall = view.findViewById(R.id.cekall)
    }

    private fun hapus() {
        val alertDialog: LottieAlertDialog =
            LottieAlertDialog.Builder(requireActivity(), DialogTypes.TYPE_WARNING)
                .setTitle("Yakin ingin menghapus?")
                .setDescription("Produk yang dipilih akan terhapus secara permanen.")
                .setPositiveText("Iya")
                .setPositiveTextColor(Color.parseColor("#ffeaea"))
                .setPositiveButtonColor(Color.parseColor("#f44242"))
                .setPositiveListener(object : ClickListener {
                    override fun onClick(dialog: LottieAlertDialog) {
                        val listDelete = ArrayList<Produk>()
                        for (p in listProduk) {
                            if (p.selected) listDelete.add(p)
                        }
                        delete(listDelete)
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

    private fun setError(pesan: String) {
        var alertDialog : LottieAlertDialog = LottieAlertDialog.Builder(requireActivity(), DialogTypes.TYPE_ERROR)
            .setTitle("Something error")
            .setDescription(pesan)
            .setPositiveText("Oke")
            .setPositiveTextColor(Color.WHITE)
            .setPositiveButtonColor(Color.RED)
            .setPositiveListener(object :ClickListener{
                override fun onClick(dialog: LottieAlertDialog) {
                    dialog.dismiss()
                }

            })
            .build()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    override fun onResume() {
        displayProduk()
        hitungTotal()
        super.onResume()
    }
}