package com.example.tokobuku

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
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

class UbahProfile : AppCompatActivity() {
    lateinit var btn_kembali: ImageView
    lateinit var l_name: TextInputLayout
    lateinit var e_name: TextInputEditText
    lateinit var l_email: TextInputLayout
    lateinit var e_email: TextInputEditText
    lateinit var l_phone: TextInputLayout
    lateinit var e_phone: TextInputEditText
    lateinit var btn_simpan: CardView
    lateinit var progress: ProgressBar
    lateinit var txt_simpan: TextView
    lateinit var s: SharedPref
    lateinit var nama: String
    lateinit var email: String
    lateinit var phone: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ubah_profile)
        s = SharedPref(this)
        setInit()
        val user = s.getUser()!!
        e_name.setText(user.nama)
        e_email.setText(user.email)
        e_phone.setText(user.phone)
        setButton()
        cekvalidasi()
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
        e_email.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (e_email.text.toString().isEmpty()) {
                    l_email.isErrorEnabled = false
                } else if (Patterns.EMAIL_ADDRESS.matcher(e_email.text.toString()).matches()) {
                    l_email.isErrorEnabled = false
                } else if (e_email.text.toString().isNotEmpty()) {
                    l_email.isErrorEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        e_phone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (e_phone.text.toString().isEmpty()) {
                    l_phone.isErrorEnabled = false
                } else if (e_phone.text.toString().isNotEmpty()) {
                    l_phone.isErrorEnabled = false
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
                ubaprofile()
            }
        }
    }

    private fun ubaprofile() {
        nama = e_name.text.toString()
        email = e_email.text.toString()
        phone = e_phone.text.toString()
        val id = s.getUser()!!.id

        progress.visibility = View.VISIBLE
        txt_simpan.visibility = View.GONE
        ApiConfig.instanceRetrofit.ubahprofile(id, nama, email, phone)
            .enqueue(object : Callback<ResponsModel> {
                override fun onResponse(
                    call: Call<ResponsModel>,
                    response: Response<ResponsModel>
                ) {
                    progress.visibility = View.GONE
                    txt_simpan.visibility = View.VISIBLE
                    val respon = response.body()!!
                    if (respon.success == 1) {
                        sukses("Profile berhasil diubah!")
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
        if (e_name.text.toString().isEmpty()) {
            l_name.isErrorEnabled = true
            l_name.error = "Kolom nama tidak boleh kosong!"
            e_name.requestFocus()
            return false
        }
        if (e_email.text.toString().isEmpty()) {
            l_email.isErrorEnabled = true
            l_email.error = "Kolom email tidak boleh kosong!"
            e_email.requestFocus()
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(e_email.text.toString()).matches()) {
            l_email.isErrorEnabled = true
            l_email.error = "Format email salah!. Contoh: gunakan @example.com"
            e_email.requestFocus()
            return false
        }

        if (e_phone.text.toString().isEmpty()) {
            l_phone.isErrorEnabled = true
            l_phone.error = "Kolom phone tidak boleh kosong!"
            e_phone.requestFocus()
            return false
        }
        return true
    }

    private fun setInit() {
        btn_kembali = findViewById(R.id.btn_kembali)
        l_name = findViewById(R.id.l_name)
        e_name = findViewById(R.id.e_name)
        l_email = findViewById(R.id.l_email)
        e_email = findViewById(R.id.e_email)
        l_phone = findViewById(R.id.l_phone)
        e_phone = findViewById(R.id.e_phone)
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
                    val intent = Intent(this@UbahProfile, HomeActivity::class.java)
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