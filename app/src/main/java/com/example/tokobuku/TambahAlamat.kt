package com.example.tokobuku

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import com.example.tokobuku.core.data.model.Alamat
import com.example.tokobuku.core.data.model.ModelAlamat
import com.example.tokobuku.core.data.model.ResponsModel
import com.example.tokobuku.core.data.room.MyDatabase
import com.example.tokobuku.core.data.source.ApiConfigAlamat
import com.example.tokobuku.ui.auth.LoginActivity
import com.example.tokobuku.util.ApiKey
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.labters.lottiealertdialoglibrary.ClickListener
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog
import com.toptoche.searchablespinnerlibrary.SearchableSpinner
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TambahAlamat : AppCompatActivity() {

    lateinit var btn_kembali: ImageView
    lateinit var sp_prov: SearchableSpinner
    lateinit var sp_kota: SearchableSpinner
    lateinit var progress: ProgressBar
    lateinit var div_provinsi: RelativeLayout
    lateinit var div_kota: RelativeLayout
    lateinit var div_kecamatan: RelativeLayout
    lateinit var btn_simpan: CardView
    lateinit var l_name: TextInputLayout
    lateinit var e_name: TextInputEditText
    lateinit var l_nohp: TextInputLayout
    lateinit var e_nohp: TextInputEditText
    lateinit var l_alamat: TextInputLayout
    lateinit var e_alamat: TextInputEditText
    lateinit var l_alamat2: TextInputLayout
    lateinit var e_alamat2: TextInputEditText
    lateinit var l_kodepos: TextInputLayout
    lateinit var e_kodepos: TextInputEditText
    lateinit var progress2: ProgressBar
    lateinit var text_simpan: TextView

    var provinsi = ModelAlamat.Provinsi()
    var kota = ModelAlamat.Provinsi()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_alamat)
        setInit()
        setButton()
        getProvinsi()
        cekvalidasi()
    }

    private fun getProvinsi() {
        progress.visibility = View.VISIBLE
        ApiConfigAlamat.instanceRetrofit.getProvinsi(ApiKey.key)
            .enqueue(object : Callback<ResponsModel> {
                override fun onResponse(
                    call: Call<ResponsModel>,
                    response: Response<ResponsModel>
                ) {
                    if (response.isSuccessful) {
                        progress.visibility = View.GONE
                        div_provinsi.visibility = View.VISIBLE

                        val res = response.body()!!
                        val arrayString = ArrayList<String>()
                        arrayString.add("Pilih Provinsi")

                        val listPovinsi = res.rajaongkir.results
                        for (prov in listPovinsi) {
                            arrayString.add(prov.province)
                        }
                        val adapter = ArrayAdapter<Any>(
                            this@TambahAlamat,
                            R.layout.item_spinner,
                            arrayString.toTypedArray()
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        sp_prov.adapter = adapter
                        sp_prov.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    if (position != 0) {
                                        provinsi = listPovinsi[position - 1]
                                        val idProv = provinsi.province_id
                                        getKota(idProv)
                                    }
                                }

                                override fun onNothingSelected(parent: AdapterView<*>?) {

                                }
                            }
                    } else {
                        Log.d("Error", "gagal memuat data" + response.message())
                        setError(response.message())
                    }
                }

                override fun onFailure(call: Call<ResponsModel>, t: Throwable) {
                    Log.d("Error", "gagal memuat data" + t.message)
                    setError(t.message.toString())
                }
            })
    }

    fun getKota(id: String) {
        progress.visibility = View.VISIBLE
        ApiConfigAlamat.instanceRetrofit.getKota(ApiKey.key, id)
            .enqueue(object : Callback<ResponsModel> {
                override fun onResponse(
                    call: Call<ResponsModel>,
                    response: Response<ResponsModel>
                ) {
                    if (response.isSuccessful) {
                        progress.visibility = View.GONE
                        div_kota.visibility = View.VISIBLE

                        val res = response.body()!!
                        val arrayString = ArrayList<String>()
                        val listArry = res.rajaongkir.results
                        arrayString.add("Pilih Kota")
                        for (kota in listArry) {
                            arrayString.add(kota.city_name + " " + kota.postal_code)
                        }
                        val adapter = ArrayAdapter<Any>(
                            this@TambahAlamat,
                            R.layout.item_spinner,
                            arrayString.toTypedArray()
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        sp_kota.adapter = adapter
                        sp_kota.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    if (position != 0) {
                                        kota = listArry[position - 1]
                                        val kodepos = kota.postal_code
                                        e_kodepos.setText(kodepos)
                                    }
                                }

                                override fun onNothingSelected(parent: AdapterView<*>?) {

                                }

                            }
                    } else {
                        Log.d("Error", "gagal memuat data" + response.message())
                        setError(response.message())
                    }
                }

                override fun onFailure(call: Call<ResponsModel>, t: Throwable) {
                    setError(t.message.toString())
                }

            })
    }

    private fun cekvalidasi() {
        e_name.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (e_name.text.toString().isEmpty()) {
                    l_name.isErrorEnabled = false
                } else if (e_name.text.toString().isNotEmpty()) {
                    l_name.isErrorEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
        e_nohp.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (e_nohp.text.toString().isEmpty()) {
                    l_nohp.isErrorEnabled = false
                } else if (e_nohp.text.toString().isNotEmpty()) {
                    l_nohp.isErrorEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
        e_alamat.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (e_alamat.text.toString().isEmpty()) {
                    l_alamat.isErrorEnabled = false
                } else if (e_alamat.text.toString().isNotEmpty()) {
                    l_alamat.isErrorEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
        e_alamat2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (e_alamat2.text.toString().isEmpty()) {
                    l_alamat2.isErrorEnabled = false
                } else if (e_alamat2.text.toString().isNotEmpty()) {
                    l_alamat2.isErrorEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
        e_kodepos.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (e_kodepos.text.toString().isEmpty()) {
                    l_kodepos.isErrorEnabled = false
                } else if (e_kodepos.text.toString().isNotEmpty()) {
                    l_kodepos.isErrorEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
    }

    private fun setButton() {
        btn_kembali.setOnClickListener {
            onBackPressed()
        }
        btn_simpan.setOnClickListener {
            if (validasi()) {
                simpan()
            }
        }
    }

    private fun simpan() {
        if (provinsi.province_id == "0") {
            Toast.makeText(this, "Silahkan pilih provinsi", Toast.LENGTH_SHORT).show()
            return
        }
        if (kota.city_id == "0") {
            Toast.makeText(this, "Silahkan pilih kota", Toast.LENGTH_SHORT).show()
            return
        }
        val alamat = Alamat()
        alamat.name = e_name.text.toString()
        alamat.type = e_alamat.text.toString()
        alamat.phone = e_nohp.text.toString()
        alamat.alamat = e_alamat2.text.toString()
        alamat.kodepos = e_kodepos.text.toString()

        alamat.id_provinsi = Integer.valueOf(provinsi.province_id)
        alamat.provinsi = provinsi.province
        alamat.id_kota = Integer.valueOf(kota.city_id)
        alamat.kota = kota.city_name
        insert(alamat)
    }

    private fun insert(data: Alamat) {
        progress2.visibility = View.VISIBLE
        text_simpan.visibility = View.GONE
        val myDb = MyDatabase.getInstance(this)!!
        if (myDb.daoAlamat().getBystatus(true) == null) {
            progress2.visibility = View.GONE
            text_simpan.visibility = View.VISIBLE
            data.isSelected = true
        }
        CompositeDisposable().add(Observable.fromCallable { myDb.daoAlamat().insert(data) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                progress2.visibility = View.GONE
                text_simpan.visibility = View.VISIBLE
                sukses("Alamat telah berhasil disimpan")
                for (alamat in myDb.daoAlamat().getAll()) {
                    Log.d(
                        "Alamat",
                        "nama:  " + alamat.name + " - " + alamat.alamat + " - " + alamat.kota + " - " + alamat.id_kota
                    )
                }
            })
    }

    private fun validasi(): Boolean {
        if (e_name.text.toString().isEmpty()) {
            l_name.isErrorEnabled = true
            l_name.error = "Kolom nama tidak boleh kosong!"
            e_name.requestFocus()
            return false
        }
        if (e_nohp.text.toString().isEmpty()) {
            l_nohp.isErrorEnabled = true
            l_nohp.error = "Kolom phone tidak boleh kosong!"
            e_nohp.requestFocus()
            return false
        }
        if (e_alamat.text.toString().isEmpty()) {
            l_alamat.isErrorEnabled = true
            l_alamat.error = "Kolom email tidak boleh kosong!"
            e_alamat.requestFocus()
            return false
        }
        if (e_alamat2.text.toString().isEmpty()) {
            l_alamat2.isErrorEnabled = true
            l_alamat2.error = "Kolom email tidak boleh kosong!"
            e_alamat2.requestFocus()
            return false
        }
        if (e_kodepos.text.toString().isEmpty()) {
            l_kodepos.isErrorEnabled = true
            l_kodepos.error = "Kolom kode pos tidak boleh kosong!"
            e_kodepos.requestFocus()
            return false
        }

        return true
    }

    private fun setInit() {
        btn_kembali = findViewById(R.id.btn_kembali)
        sp_prov = findViewById(R.id.sp_provinsi)
        sp_kota = findViewById(R.id.sp_kota)
        progress = findViewById(R.id.progressbar)
        div_provinsi = findViewById(R.id.div_provinsi)
        div_kota = findViewById(R.id.div_kota)
        div_kecamatan = findViewById(R.id.div_kecamatan)
        btn_simpan = findViewById(R.id.btn_simpan)
        l_name = findViewById(R.id.l_name)
        e_name = findViewById(R.id.e_name)
        l_nohp = findViewById(R.id.l_nohp)
        e_nohp = findViewById(R.id.e_nohp)
        l_alamat = findViewById(R.id.l_alamat)
        e_alamat = findViewById(R.id.e_alamat)
        l_alamat2 = findViewById(R.id.l_alamat2)
        e_alamat2 = findViewById(R.id.e_alamat2)
        l_kodepos = findViewById(R.id.l_kodepos)
        e_kodepos = findViewById(R.id.e_kodepos)
        progress2 = findViewById(R.id.progress)
        text_simpan = findViewById(R.id.text_simpan)
    }

    private fun sukses(pesan: String) {
        var alertDialog: LottieAlertDialog
        alertDialog = LottieAlertDialog.Builder(this, DialogTypes.TYPE_SUCCESS)
            .setTitle("Sukses")
            .setDescription(pesan)
            .setPositiveText("Ok")
            .setPositiveTextColor(Color.WHITE)
            .setPositiveListener(object : ClickListener {
                override fun onClick(dialog: LottieAlertDialog) {
                    onBackPressed()
                    dialog.dismiss()
                }
            })
            .build()
        alertDialog.setCancelable(false)
        alertDialog.show()
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
}