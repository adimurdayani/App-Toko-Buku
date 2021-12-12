package com.example.tokobuku.ui.riwayat

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.tokobuku.DetailRiwayatActivity
import com.example.tokobuku.R
import com.example.tokobuku.core.data.adapter.AdapterRiwayat
import com.example.tokobuku.core.data.model.ResponsModel
import com.example.tokobuku.core.data.model.Transaksi
import com.example.tokobuku.core.data.source.ApiConfig
import com.example.tokobuku.util.SharedPref
import com.google.gson.Gson
import com.labters.lottiealertdialoglibrary.ClickListener
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RiwayatFragment : Fragment() {
    lateinit var search: SearchView
    lateinit var total_list: TextView
    lateinit var sw_data: SwipeRefreshLayout
    lateinit var rc_data: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_riwayat, container, false)
        setinit(view)
        getRiwayat()
        return view
    }

    private fun getRiwayat() {
        val id = SharedPref(requireActivity()).getUser()?.id
        sw_data.isRefreshing = true
        if (id != null) {
            ApiConfig.instanceRetrofit.getRiwayat(id).enqueue(object : Callback<ResponsModel> {
                override fun onResponse(
                    call: Call<ResponsModel>,
                    response: Response<ResponsModel>,
                ) {
                    sw_data.isRefreshing = false
                    val res = response.body()
                    if (res!!.success == 1) {
                        displayRiwayat(res.transaksis)
                    } else {
                        sw_data.isRefreshing = false
                        Log.d("Respon", "Error: " + res.message)
                        setError(response.message())
                    }
                }

                override fun onFailure(call: Call<ResponsModel>, t: Throwable) {
                    sw_data.isRefreshing = false
                    Log.d("Respon", "Error: " + t.message)
                    setError(t.message.toString())
                }
            })
        }
    }

    fun displayRiwayat(array: ArrayList<Transaksi>) {
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        total_list.text = array.size.toString()

        rc_data.adapter = AdapterRiwayat(array, object : AdapterRiwayat.Listeners {
            override fun onClicked(data: Transaksi) {
                val json = Gson().toJson(data, Transaksi::class.java)
                val inten = Intent(requireActivity(), DetailRiwayatActivity::class.java)
                inten.putExtra("transaksi", json)
                startActivity(inten)
            }

        })
        rc_data.layoutManager = layoutManager

        sw_data.setOnRefreshListener {
            getRiwayat()
        }

        val adapter = AdapterRiwayat(array, object : AdapterRiwayat.Listeners {
            override fun onClicked(data: Transaksi) {

            }
        })
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapter.getSearchData().filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                getRiwayat()
                return false
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

    private fun setinit(view: View) {
        search = view.findViewById(R.id.search)
        total_list = view.findViewById(R.id.total_list)
        sw_data = view.findViewById(R.id.sw_data)
        rc_data = view.findViewById(R.id.rc_data)
    }

    override fun onResume() {
        getRiwayat()
        super.onResume()
    }
}