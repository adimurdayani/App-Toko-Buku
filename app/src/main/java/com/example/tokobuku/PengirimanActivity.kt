package com.example.tokobuku

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.tokobuku.core.data.adapter.AdapterKurir
import com.example.tokobuku.core.data.model.Checkout
import com.example.tokobuku.core.data.model.rajaongkir.Costs
import com.example.tokobuku.core.data.model.rajaongkir.ResponsOngkir
import com.example.tokobuku.core.data.room.MyDatabase
import com.example.tokobuku.core.data.source.ApiConfigAlamat
import com.example.tokobuku.util.ApiKey
import com.example.tokobuku.util.Helper
import com.example.tokobuku.util.SharedPref
import com.google.gson.Gson
import com.labters.lottiealertdialoglibrary.ClickListener
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PengirimanActivity : AppCompatActivity() {
    lateinit var btn_kembali: ImageView
    lateinit var btn_alamat: LinearLayout
    lateinit var div_kosong: TextView
    lateinit var div_alamat: CardView
    lateinit var tv_nama_pengguna: TextView
    lateinit var tv_nohp: TextView
    lateinit var tv_alamat: TextView
    lateinit var btn_teksalamat: TextView
    lateinit var rc_model: RecyclerView
    lateinit var div_nometode: TextView
    lateinit var div_metode: LinearLayout
    lateinit var sp_metode: Spinner
    lateinit var total_belanja: TextView
    lateinit var biaya_pengiriman: TextView
    lateinit var total: TextView
    lateinit var sw_data: SwipeRefreshLayout
    lateinit var btn_bayar: LinearLayout
    lateinit var progress: ProgressBar
    lateinit var text_beli: TextView
    lateinit var edt_catatan: EditText
    lateinit var div_layout: LinearLayout
    lateinit var div_tidakada: TextView

    lateinit var myDb: MyDatabase
    var totalHarga = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pengiriman)
        myDb = MyDatabase.getInstance(this)!!
        setInit()

        totalHarga = Integer.valueOf(intent.getStringExtra("extra")!!)
        total_belanja.text = Helper().formatRupiah(totalHarga)
        setButton()
        setSpinner()
    }

    var ongkir = ""
    var jasaKirim = ""
    var kurir = ""
    private fun setDisplay(_kurir: String, arrayList: ArrayList<Costs>) {
        var arrayOngkir = ArrayList<Costs>()
        for (i in arrayList.indices) {
            val ongkir = arrayList[i]
            if (i == 0) {
                ongkir.isActive = true
            }
            arrayOngkir.add(ongkir)
        }
        setTotal(arrayOngkir[0].cost[0].value)
        ongkir = arrayOngkir[0].cost[0].value
        kurir = _kurir
        jasaKirim = arrayOngkir[0].service

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        var adapter: AdapterKurir? = null
        adapter = AdapterKurir(arrayOngkir, _kurir, object : AdapterKurir.Listeners {
            override fun onClicked(data: Costs, index: Int) {
                val newarrayOngkir = ArrayList<Costs>()
                for (ongkir in arrayOngkir) {
                    ongkir.isActive = data.description == ongkir.description
                    newarrayOngkir.add(ongkir)
                }
                arrayOngkir = newarrayOngkir
                adapter!!.notifyDataSetChanged()
                setTotal(data.cost[0].value)

                ongkir = data.cost[0].value
                kurir = _kurir
                jasaKirim = data.service
            }
        })
        rc_model.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        rc_model.adapter = adapter
        rc_model.layoutManager = layoutManager
    }

    fun setTotal(ongkir: String) {
        total.text = Helper().formatRupiah(Integer.valueOf(ongkir) + totalHarga)
        biaya_pengiriman.text = Helper().formatRupiah(ongkir)
    }

    private fun setSpinner() {
        val arrayString = ArrayList<String>()
        arrayString.add("JNE")
        arrayString.add("POS")
        arrayString.add("TIKI")
        val adapter = ArrayAdapter<Any>(this, R.layout.item_spinner, arrayString.toTypedArray())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sp_metode.adapter = adapter
        sp_metode.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position != 0) {
                        getOngkir(sp_metode.selectedItem.toString())
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }
    }

    private fun setButton() {
        btn_alamat.setOnClickListener {
            startActivity(Intent(this, ListAlamat::class.java))
        }

        btn_kembali.setOnClickListener {
            onBackPressed()
        }

        btn_bayar.setOnClickListener {
            bayar()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun cekalamat() {
        if (myDb.daoAlamat().getBystatus(true) != null) {
            div_alamat.visibility = View.VISIBLE
            div_kosong.visibility = View.GONE
            div_metode.visibility = View.VISIBLE
            div_layout.visibility = View.VISIBLE
            div_tidakada.visibility = View.GONE

            val getalamat = myDb.daoAlamat().getBystatus(true)!!
            tv_nama_pengguna.text = getalamat.name
            tv_nohp.text = getalamat.phone
            tv_alamat.text =
                getalamat.alamat + ", " + getalamat.kota + ", " + getalamat.kodepos + ", (" + getalamat.type + ")"
            btn_teksalamat.text = "Ubah Alamat"

            getOngkir("JNE")
        } else {

            div_layout.visibility = View.GONE
            div_alamat.visibility = View.GONE
            div_kosong.visibility = View.VISIBLE
            div_tidakada.visibility = View.VISIBLE
            btn_teksalamat.text = "Tambah Alamat"
        }

    }

    fun getOngkir(kurir: String) {
        val alamat = myDb.daoAlamat().getBystatus(true)
        val origin = "501"
        val destination = "" + alamat?.id_kota.toString()
        val berat = 1000

        sw_data.isRefreshing = true
        ApiConfigAlamat.instanceRetrofit.ongkir(
            ApiKey.key,
            origin,
            destination,
            berat,
            kurir.toLowerCase()
        )
            .enqueue(object : Callback<ResponsOngkir> {
                override fun onResponse(
                    call: Call<ResponsOngkir>,
                    response: Response<ResponsOngkir>
                ) {
                    if (response.isSuccessful) {
                        sw_data.isRefreshing = false
                        Log.d("Sukses", "memuat data: " + response.message())
                        val result = response.body()!!.rajaongkir.results
                        if (result.isNotEmpty()) {
                            setDisplay(result[0].code.toUpperCase(), result[0].costs)
                        }
                    } else {
                        Log.d("Error", "gagal : " + response.message())
                        error(response.message())
                    }
                }

                override fun onFailure(call: Call<ResponsOngkir>, t: Throwable) {
                    Log.d("Error", "gagal memuat data: $t")
                    setError("Terjadi kesalahan koneksi!")
                }

            })
    }

    private fun setError(pesan: String) {
        var alertDialog: LottieAlertDialog = LottieAlertDialog.Builder(this, DialogTypes.TYPE_ERROR)
            .setTitle("Something error")
            .setDescription(pesan)
            .setPositiveText("Oke")
            .setPositiveTextColor(Color.WHITE)
            .setPositiveButtonColor(Color.RED)
            .setPositiveListener(object : ClickListener {
                override fun onClick(dialog: LottieAlertDialog) {
                    dialog.dismiss()
                }

            })
            .build()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun bayar() {
        val user = SharedPref(this).getUser()
        val listProduk = myDb.daoKeranjang().getAll() as ArrayList
        var totalItem = 0
        var totalHarga = 0
        val getalamat = myDb.daoAlamat().getBystatus(true)!!
        val produks = ArrayList<Checkout.Item>()

        for (p in listProduk) {
            if (p.selected) {
                totalItem += p.jumlah
                totalHarga += (p.jumlah * Integer.valueOf(p.harga))

                val produk = Checkout.Item()
                produk.id = "" + p.id.toString()
                produk.total_item = "" + p.jumlah
                produk.total_harga = "" + (p.jumlah * Integer.valueOf(p.harga))
                produk.catatan = p.deskripsi

                produks.add(produk)
            }
        }
        val checkout = Checkout()
        checkout.kostumer_id = "" + user!!.id.toString()
        checkout.total_item = "" + totalItem
        checkout.total_harga = "" + totalHarga
        checkout.name = getalamat.name
        checkout.phone = getalamat.phone
        checkout.jasa_pengiriman = jasaKirim
        checkout.ongkir = ongkir
        checkout.kurir = kurir
        checkout.deskripsi = edt_catatan.text.toString()
        checkout.detail_lokasi = tv_alamat.text.toString()
        checkout.total_transfer = "" + (totalHarga + Integer.valueOf(ongkir))
        checkout.produks = produks
        checkout.toko_id = listProduk[0].toko_id.toString()

        val json = Gson().toJson(checkout, Checkout::class.java)
        Log.d("Response: ", "json: $json")
        val intent = Intent(this, PembayaranActivity::class.java)
        intent.putExtra("extra", json)
        startActivity(intent)
        finish()
    }

    private fun setInit() {
        btn_alamat = findViewById(R.id.btnalamat)
        btn_kembali = findViewById(R.id.btn_kembali)
        div_kosong = findViewById(R.id.div_kosong)
        div_alamat = findViewById(R.id.div_alamat)
        tv_nama_pengguna = findViewById(R.id.nama_pengguna)
        tv_nohp = findViewById(R.id.nohp)
        tv_alamat = findViewById(R.id.alamat)
        btn_teksalamat = findViewById(R.id.btn_teksalamat)
        rc_model = findViewById(R.id.rc_metode)
        div_metode = findViewById(R.id.div_metodepengiriman)
        div_nometode = findViewById(R.id.div_nometode)
        sp_metode = findViewById(R.id.sp_metode)
        total_belanja = findViewById(R.id.total_belanja)
        biaya_pengiriman = findViewById(R.id.biaya_pengiriman)
        total = findViewById(R.id.tv_totalharga)
        sw_data = findViewById(R.id.sw_data)
        btn_bayar = findViewById(R.id.btn_bayar)
        progress = findViewById(R.id.progress)
        text_beli = findViewById(R.id.text_beli)
        edt_catatan = findViewById(R.id.edt_catatan)
        div_layout = findViewById(R.id.div_layout)
        div_tidakada = findViewById(R.id.div_tidakada)
    }

    override fun onResume() {
        cekalamat()
        super.onResume()
    }
}