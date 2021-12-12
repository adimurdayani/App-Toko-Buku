package com.example.tokobuku

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.tokobuku.core.data.model.Bank
import com.example.tokobuku.core.data.model.Checkout
import com.example.tokobuku.core.data.model.Transaksi
import com.example.tokobuku.core.data.room.MyDatabase
import com.example.tokobuku.util.Helper
import com.google.gson.Gson

class SuksesActivity : AppCompatActivity() {
    lateinit var nomor_bank: TextView
    lateinit var nama_penerima: TextView
    lateinit var total_pembayaran: TextView
    lateinit var img_bank: ImageView
    lateinit var btn_kembali: ImageView
    lateinit var btn_cekstatus: LinearLayout
    lateinit var btn_copy: ImageView
    lateinit var btn_copy2: ImageView
    var nominal = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sukses)
        setInit()
        setValue()
        setButton()
    }

    private fun setButton() {
        btn_kembali.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
        btn_cekstatus.setOnClickListener {
            val intent  = Intent("event:riwayat")
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            onBackPressed()
        }
        btn_copy.setOnClickListener {
            copyText(nomor_bank.text.toString())
        }
        btn_copy2.setOnClickListener {
            copyText(nominal.toString())
        }
    }

    private fun copyText(text: String) {
        val copyManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val copyText = ClipData.newPlainText("text", text)
        copyManager.setPrimaryClip(copyText)
        Toast.makeText(this, "Copy to clipboard", Toast.LENGTH_SHORT).show()
    }

    private fun setValue() {
        val jsBank = intent.getStringExtra("bank")
        val jsTransaksi = intent.getStringExtra("transaksi")
        val jsCheckout = intent.getStringExtra("checkout")

        val bank = Gson().fromJson(jsBank, Bank::class.java)
        val transaksi = Gson().fromJson(jsTransaksi, Transaksi::class.java)
        val checkout = Gson().fromJson(jsCheckout, Checkout::class.java)

//        hapus keranjang
        val myDb = MyDatabase.getInstance(this)!!
        for (produk in checkout.produks){
            myDb.daoKeranjang().deleteById(produk.id)
        }

        nomor_bank.text = bank.rekening
        nama_penerima.text = bank.penerima
        img_bank.setImageResource(bank.image)

        nominal = Integer.valueOf(transaksi.total_transfer) + transaksi.kode_unik
        total_pembayaran.text = Helper().formatRupiah(nominal)
    }

    private fun setInit() {
        nomor_bank = findViewById(R.id.nomor_bank)
        nama_penerima = findViewById(R.id.nama_penerima)
        total_pembayaran = findViewById(R.id.total_pembayaran)
        img_bank = findViewById(R.id.img_bank)
        btn_kembali = findViewById(R.id.btn_kembali)
        btn_cekstatus = findViewById(R.id.btn_cekstatus)
        btn_copy = findViewById(R.id.btn_copy)
        btn_copy2 = findViewById(R.id.btn_copy2)
    }
}