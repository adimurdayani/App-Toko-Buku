package com.example.tokobuku.ui.home

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.tokobuku.AllProdukActivity
import com.example.tokobuku.R
import com.example.tokobuku.core.data.adapter.AdapterProduk
import com.example.tokobuku.core.data.adapter.AdapterSliderView
import com.example.tokobuku.core.data.model.Produk
import com.example.tokobuku.core.data.model.ResponsModel
import com.example.tokobuku.core.data.source.ApiConfig
import com.example.tokobuku.util.SharedPref
import com.facebook.shimmer.ShimmerFrameLayout
import com.labters.lottiealertdialoglibrary.ClickListener
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog
import com.smarteist.autoimageslider.SliderView
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {
    lateinit var shimmerFrameLayout: ShimmerFrameLayout
    lateinit var rc_data: RecyclerView
    lateinit var rc_data2: RecyclerView
    lateinit var rc_data3: RecyclerView
    lateinit var sw_data1: SwipeRefreshLayout
    lateinit var btn_all: TextView
    lateinit var btn_all2: TextView
    lateinit var s: SharedPref
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        setinit(view)
        s = SharedPref(requireActivity())
        setButton()
        return view
    }

    private var listProdukterbaru: ArrayList<Produk> = ArrayList()
    private var listProdukterlaris: ArrayList<Produk> = ArrayList()
    private fun setDisplay() {
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        rc_data.adapter = AdapterProduk(requireActivity(), listProdukterlaris)
        rc_data.layoutManager = layoutManager

        val layoutManager2 = LinearLayoutManager(activity)
        layoutManager2.orientation = LinearLayoutManager.HORIZONTAL
        rc_data2.adapter = AdapterProduk(requireActivity(), listProdukterbaru)
        rc_data2.layoutManager = layoutManager2

        sw_data1.setOnRefreshListener { getProdukTerbaru() }
    }

    private fun getProdukTerlaris() {
        sw_data1.isRefreshing = true
        shimmerFrameLayout.visibility = View.VISIBLE
        ApiConfig.instanceRetrofit.produkTerlaris("TERLARIS").enqueue(object : Callback<ResponsModel> {
            override fun onResponse(
                call: Call<ResponsModel>,
                response: Response<ResponsModel>,
            ) {
                sw_data1.isRefreshing = false
                shimmerFrameLayout.visibility = View.GONE
                val res = response.body()!!
                if (res.success == 1) {
                    listProdukterlaris = res.produk
                    setDisplay()
                } else {
                    setError(res.message)
                }
            }

            override fun onFailure(call: Call<ResponsModel>, t: Throwable) {
                sw_data1.isRefreshing = false
                shimmerFrameLayout.visibility = View.GONE
                setError("Terjadi kesalahan koneksi!")
                Log.d("Response", "Error: " + t.message)
            }
        })
    }

    private fun getProdukTerbaru() {
        sw_data1.isRefreshing = true
        shimmerFrameLayout.visibility = View.VISIBLE
        ApiConfig.instanceRetrofit.produkTerbaru("BARU").enqueue(object : Callback<ResponsModel> {
            override fun onResponse(
                call: Call<ResponsModel>,
                response: Response<ResponsModel>,
            ) {
                sw_data1.isRefreshing = false
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
                sw_data1.isRefreshing = false
                shimmerFrameLayout.visibility = View.GONE
                setError("Terjadi kesalahan koneksi!")
                Log.d("Response", "Error: " + t.message)
            }
        })
    }

    private fun setError(pesan: String) {
        var alertDialog: LottieAlertDialog =
            LottieAlertDialog.Builder(requireActivity(), DialogTypes.TYPE_ERROR)
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

    private fun setButton() {
        btn_all.setOnClickListener {
            val intent = Intent(requireActivity(), AllProdukActivity::class.java)
            intent.putExtra("kategori", "BARU")
            requireActivity().startActivity(intent)
        }

        btn_all2.setOnClickListener {
            val intent = Intent(requireActivity(), AllProdukActivity::class.java)
            intent.putExtra("kategori", "TERLARIS")
            requireActivity().startActivity(intent)
        }
    }

    private fun setinit(view: View) {
        val imageSlider = view.findViewById<SliderView>(R.id.imageSlider)
        rc_data = view.findViewById(R.id.rc_terlaris)
        rc_data2 = view.findViewById(R.id.rc_terbaru)
        rc_data3 = view.findViewById(R.id.rc_kategori)
        sw_data1 = view.findViewById(R.id.sw_data1)
        btn_all = view.findViewById(R.id.btn_all)
        btn_all2 = view.findViewById(R.id.btn_all2)
        shimmerFrameLayout = view.findViewById(R.id.shimmer)

        val imageList: ArrayList<Int> = ArrayList()
        imageList.add(R.drawable.slide1)
        imageList.add(R.drawable.slide2)
        imageList.add(R.drawable.slide3)
        setImageInSlider(imageList, imageSlider)
    }

    private fun setImageInSlider(images: ArrayList<Int>, imageSlider: SliderView) {
        val adapter = AdapterSliderView()
        adapter.renewItems(images)
        imageSlider.setSliderAdapter(adapter)
        imageSlider.isAutoCycle = true
        imageSlider.startAutoCycle()
    }

    override fun onResume() {
        getProdukTerbaru()
        getProdukTerlaris()
        shimmerFrameLayout.startShimmerAnimation()
        super.onResume()
    }

    override fun onPause() {
        shimmerFrameLayout.stopShimmerAnimation()
        super.onPause()
    }
}