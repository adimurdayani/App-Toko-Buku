package com.example.tokobuku

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tokobuku.core.data.adapter.AdapterBank
import com.example.tokobuku.core.data.model.Bank
import com.example.tokobuku.core.data.model.Checkout
import com.example.tokobuku.core.data.model.ResponsModel
import com.example.tokobuku.core.data.model.Transaksi
import com.example.tokobuku.core.data.source.ApiConfig
import com.example.tokobuku.ui.auth.LoginActivity
import com.google.gson.Gson
import com.labters.lottiealertdialoglibrary.ClickListener
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PembayaranActivity : AppCompatActivity() {
    lateinit var rc_data: RecyclerView
    lateinit var btn_kembali: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pembayaran)
        setInit()
        setButton()
        displayBank()
    }

    private fun displayBank() {
        val arrarBank = ArrayList<Bank>()
        arrarBank.add(Bank("Bank BCA", "091271231010", "Adi Murdayani", R.drawable.bca))
        arrarBank.add(Bank("Bank BRI", "019271231289", "Dewi Astuti", R.drawable.bri))
        arrarBank.add(Bank("Bank Mandiri", "090912837110", "Murdayani", R.drawable.mandiri))

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        rc_data.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        rc_data.layoutManager = layoutManager
        rc_data.adapter = AdapterBank(arrarBank, object : AdapterBank.Listeners {
            override fun onCreate(data: Bank, index: Int) {
                bayar(data)
            }
        })
    }

    private fun bayar(bank: Bank) {
        val json = intent.getStringExtra("extra")!!.toString()
        val checkout = Gson().fromJson(json, Checkout::class.java)
        checkout.bank = bank.nama

        val alertDialog: LottieAlertDialog =
            LottieAlertDialog.Builder(this, DialogTypes.TYPE_LOADING)
                .setTitle("Loading")
                .setDescription("Please Wait")
                .build()
        alertDialog.setCancelable(false)
        alertDialog.show()
        ApiConfig.instanceRetrofit.checkout(checkout)
            .enqueue(object : Callback<ResponsModel> {
                override fun onResponse(
                    call: Call<ResponsModel>,
                    response: Response<ResponsModel>,
                ) {
                    alertDialog.dismiss()
                    val res = response.body()!!
                    if (res.success == 1) {
                        val jsBank = Gson().toJson(bank, Bank::class.java)
                        val jsTransaksi = Gson().toJson(res.transaksi, Transaksi::class.java)
                        val jsCheckout = Gson().toJson(checkout, Checkout::class.java)
                        Log.d("Respon: ", "Data Bank: " + jsBank + "Data Transaksi: " + jsTransaksi)

                        val alertDialog: LottieAlertDialog = LottieAlertDialog.Builder(
                            this@PembayaranActivity,
                            DialogTypes.TYPE_SUCCESS
                        )
                            .setTitle("Sukses")
                            .setDescription("Anda telah berhasil memilih metode pembayaran, klik tombol untuk melihat detail.")
                            .setPositiveText("Ok")
                            .setPositiveTextColor(Color.WHITE)
                            .setPositiveListener(object : ClickListener {
                                override fun onClick(dialog: LottieAlertDialog) {
                                    val intent =
                                        Intent(this@PembayaranActivity, SuksesActivity::class.java)
                                    intent.putExtra("bank", jsBank)
                                    intent.putExtra("transaksi", jsTransaksi)
                                    intent.putExtra("checkout", jsCheckout)
                                    startActivity(intent)
                                    finish()
                                    dialog.dismiss()
                                }
                            })
                            .build()
                        alertDialog.setCancelable(false)
                        alertDialog.show()

                    } else {
                        setError(res.message)
                        alertDialog.dismiss()
                    }
                }

                override fun onFailure(call: Call<ResponsModel>, t: Throwable) {
                    Log.d("Message", "Error: " + t.stackTraceToString())
                    setError("Terjadi kesalahan koneksi!")
                    alertDialog.dismiss()
                }
            })
    }

    private fun setButton() {
        btn_kembali.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setInit() {
        rc_data = findViewById(R.id.rc_data)
        btn_kembali = findViewById(R.id.btn_kembali)
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

    override fun onRestart() {
        displayBank()
        super.onRestart()
    }
}