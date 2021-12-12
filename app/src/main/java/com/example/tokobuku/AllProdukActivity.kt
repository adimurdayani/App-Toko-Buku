package com.example.tokobuku

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Adapter
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.tokobuku.core.data.adapter.AdapterProduk
import com.example.tokobuku.core.data.model.Produk
import com.example.tokobuku.core.data.model.ResponsModel
import com.example.tokobuku.core.data.source.ApiConfig
import com.facebook.shimmer.ShimmerFrameLayout
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllProdukActivity : AppCompatActivity() {
    lateinit var btn_kembali : ImageView
    lateinit var judul_produk : TextView
    lateinit var search : SearchView
    lateinit var sw_data : SwipeRefreshLayout
    lateinit var rc_data : RecyclerView
    lateinit var shimmerFrameLayout: ShimmerFrameLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_produk)
        setInit()
    }

    private var listProdukterbaru: ArrayList<Produk> = ArrayList()
    @SuppressLint("SetTextI18n")
    private fun setDisplay(){
        val layoutManager = GridLayoutManager(this, 2)
        rc_data.adapter = AdapterProduk(this, listProdukterbaru)
        rc_data.layoutManager = layoutManager
        sw_data.setOnRefreshListener { getProdukTerbaru() }
        judul_produk.text = "PRODUK "+ intent.getStringExtra("kategori")

        val adapter = AdapterProduk(this, listProdukterbaru)
        search.setOnSearchClickListener { object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapter.getSearchData().filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                getProdukTerbaru()
                return false
            }

        } }
    }

    private fun getProdukTerbaru() {
        sw_data.isRefreshing = true
        shimmerFrameLayout.visibility = View.VISIBLE
        val terbaru = intent.getStringExtra("kategori")
        ApiConfig.instanceRetrofit.produk(terbaru!!).enqueue(object : Callback<ResponsModel> {
            override fun onResponse(
                call: Call<ResponsModel>,
                response: Response<ResponsModel>,
            ) {
                sw_data.isRefreshing = false
                shimmerFrameLayout.visibility = View.GONE
                val res = response.body()!!
                if (res.success == 1) {
                    listProdukterbaru = res.produk
                    setDisplay()
                } else {
                    setError(res.message)
                }
            }

            override fun onFailure(call: Call<ResponsModel>, t: Throwable) {
                sw_data.isRefreshing = false
                shimmerFrameLayout.visibility = View.GONE
                setError("Terjadi kesalahan koneksi!")
                Log.d("Response", "Error: " + t.message)
            }
        })
    }

    private fun setError(pesan: String) {
        var alertDialog: LottieAlertDialog =
            LottieAlertDialog.Builder(this, DialogTypes.TYPE_ERROR)
                .setTitle("Someting error")
                .setDescription(pesan)
                .build()
        alertDialog.show()
    }

    private fun setInit() {
        btn_kembali = findViewById(R.id.btn_kembali)
        judul_produk = findViewById(R.id.judul_produk)
        search = findViewById(R.id.search)
        sw_data = findViewById(R.id.sw_data)
        rc_data = findViewById(R.id.rc_data)
        shimmerFrameLayout = findViewById(R.id.shimmer)

        btn_kembali.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onResume() {
        getProdukTerbaru()
        shimmerFrameLayout.startShimmerAnimation()
        super.onResume()
    }

    override fun onPause() {
        shimmerFrameLayout.stopShimmerAnimation()
        super.onPause()
    }
}