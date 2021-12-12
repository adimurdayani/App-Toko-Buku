package com.example.tokobuku

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tokobuku.core.data.adapter.AdapterAlamat
import com.example.tokobuku.core.data.model.Alamat
import com.example.tokobuku.core.data.room.MyDatabase
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ListAlamat : AppCompatActivity() {
    lateinit var btn_alamat: LinearLayout
    lateinit var btn_kembali: ImageView
    lateinit var div_pesan: LinearLayout
    lateinit var rc_data: RecyclerView
    lateinit var myDb: MyDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_alamat)
        myDb = MyDatabase.getInstance(this)!!
        setInit()
        setButton()
    }

    private fun displayAlamat() {
        val arrayList = myDb.daoAlamat().getAll() as ArrayList

        if (arrayList.isEmpty()) div_pesan.visibility = View.VISIBLE
        else div_pesan.visibility = View.GONE

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        rc_data.adapter = AdapterAlamat(arrayList, object : AdapterAlamat.Listeners {
            override fun onClicked(data: Alamat) {
                if (myDb.daoAlamat().getBystatus(true) != null) {
                    val alamatActive = myDb.daoAlamat().getBystatus(true)!!
                    alamatActive.isSelected = false
                    updateActive(alamatActive, data)
                }
            }
        })
        rc_data.layoutManager = layoutManager
    }

    private fun updateActive(dataActive: Alamat, dataNonActive: Alamat) {
        CompositeDisposable().add(Observable.fromCallable {
            myDb.daoAlamat().update(dataActive)
        }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                updateNonActive(dataNonActive)
            })

    }

    private fun updateNonActive(data: Alamat) {
        data.isSelected = true
        CompositeDisposable().add(Observable.fromCallable {
            myDb.daoAlamat().update(data)
        }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                onBackPressed()
            })

    }

    private fun setButton() {
        btn_alamat.setOnClickListener {
            startActivity(Intent(this, TambahAlamat::class.java))
        }
        btn_kembali.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setInit() {
        btn_alamat = findViewById(R.id.btn_alamat)
        btn_kembali = findViewById(R.id.btn_kembali)
        div_pesan = findViewById(R.id.div_pesan)
        rc_data = findViewById(R.id.rc_data)
    }

    override fun onResume() {
        displayAlamat()
        super.onResume()
    }
}