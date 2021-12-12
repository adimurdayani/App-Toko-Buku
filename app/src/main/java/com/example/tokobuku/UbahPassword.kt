package com.example.tokobuku

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.tokobuku.core.data.model.ResponsModel
import com.example.tokobuku.core.data.source.ApiConfig
import com.example.tokobuku.ui.auth.LoginActivity
import com.example.tokobuku.util.SharedPref
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.labters.lottiealertdialoglibrary.ClickListener
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UbahPassword : AppCompatActivity() {
    lateinit var btn_kembali: ImageView
    lateinit var l_password: TextInputLayout
    lateinit var e_password: TextInputEditText
    lateinit var l_konf_password: TextInputLayout
    lateinit var e_konf_password: TextInputEditText
    lateinit var btn_simpan: CardView
    lateinit var s: SharedPref
    lateinit var progress: ProgressBar
    lateinit var txt_simpan: TextView
    lateinit var password: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ubah_password)
        s = SharedPref(this)
        setInit()
        setButton()
        cekvalidasi()
    }

    private fun cekvalidasi() {
        e_password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (e_password.text.toString().isEmpty()) {
                    l_password.isErrorEnabled = false
                } else if (e_password.text.toString().length > 7) {
                    l_password.isErrorEnabled = false
                } else if (e_password.text.toString().isNotEmpty()) {
                    l_password.isErrorEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        e_konf_password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (e_konf_password.text.toString().isEmpty()) {
                    l_konf_password.isErrorEnabled = false
                } else if (e_konf_password.text.toString().length > 7) {
                    l_konf_password.isErrorEnabled = false
                } else if (e_konf_password.text.toString()
                        .matches(e_password.text.toString().toRegex())
                ) {
                    l_konf_password.isErrorEnabled = false
                } else if (e_konf_password.text.toString().isNotEmpty()) {
                    l_konf_password.isErrorEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun setButton() {
        btn_kembali.setOnClickListener {
            onBackPressed()
        }
        btn_simpan.setOnClickListener {
            if (validasi()) {
                ubahpassword()
            }
        }
    }

    private fun ubahpassword() {
        password = e_password.text.toString()
        val id = s.getUser()!!.id

        progress.visibility = View.VISIBLE
        txt_simpan.visibility = View.GONE
        ApiConfig.instanceRetrofit.ubahpassword(id, password)
            .enqueue(object : Callback<ResponsModel> {
                override fun onResponse(
                    call: Call<ResponsModel>,
                    response: Response<ResponsModel>
                ) {
                    progress.visibility = View.GONE
                    txt_simpan.visibility = View.VISIBLE
                    val respon = response.body()!!
                    if (respon.success == 1) {
                        sukses("Password berhasil diubah!")
                    } else {
                        progress.visibility = View.GONE
                        txt_simpan.visibility = View.VISIBLE
                        setError(respon.message)
                    }
                }

                override fun onFailure(call: Call<ResponsModel>, t: Throwable) {
                    progress.visibility = View.GONE
                    txt_simpan.visibility = View.VISIBLE
                    Log.d("Respon", "Pesan: " + t.message)
                    setError(t.message.toString())
                }
            })
    }

    private fun validasi(): Boolean {
        if (e_password.text.toString().isEmpty()) {
            l_password.isErrorEnabled = true
            l_password.error = "Kolom password tidak boleh kosong!"
            e_password.requestFocus()
            return false
        } else if (e_password.text.toString().length < 6) {
            l_password.isErrorEnabled = true
            l_password.error = "Password tidak boleh kurang dari 6 karakter!"
            e_password.requestFocus()
            return false
        }
        if (e_konf_password.text.toString().isEmpty()) {
            l_konf_password.isErrorEnabled = true
            l_konf_password.error = "Kolom konfirmasi password tidak boleh kosong!"
            e_konf_password.requestFocus()
            return false
        } else if (e_konf_password.text.toString().length < 6) {
            l_konf_password.isErrorEnabled = true
            l_konf_password.error = "Konfirmasi password tidak boleh kurang dari 6 karakter!"
            e_konf_password.requestFocus()
            return false
        } else if (!e_konf_password.text.toString().matches(e_password.text.toString().toRegex())) {
            l_konf_password.isErrorEnabled = true
            l_konf_password.error = "Konfirmasi password tidak sama dengan password!"
            e_konf_password.requestFocus()
            return false
        }
        return true
    }

    private fun setInit() {
        btn_kembali = findViewById(R.id.btn_kembali)
        l_password = findViewById(R.id.l_password)
        e_password = findViewById(R.id.e_password)
        l_konf_password = findViewById(R.id.l_konf_password)
        e_konf_password = findViewById(R.id.e_konf_password)
        btn_simpan = findViewById(R.id.btn_simpan)
        progress = findViewById(R.id.progress)
        txt_simpan = findViewById(R.id.txt_simpan)
    }

    private fun sukses(pesan: String) {
        val alertDialog: LottieAlertDialog = LottieAlertDialog.Builder(
            this,
            DialogTypes.TYPE_SUCCESS
        )
            .setTitle("Sukses")
            .setDescription(pesan)
            .setPositiveText("Ok")
            .setPositiveTextColor(Color.WHITE)
            .setPositiveListener(object : ClickListener {
                override fun onClick(dialog: LottieAlertDialog) {
                    s.setStatusLogin(false)
                    val intent = Intent(this@UbahPassword, HomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
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