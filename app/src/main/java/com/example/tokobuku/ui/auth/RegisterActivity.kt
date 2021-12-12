package com.example.tokobuku.ui.auth

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
import com.example.tokobuku.R
import com.example.tokobuku.core.data.model.ResponsModel
import com.example.tokobuku.core.data.source.ApiConfig
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.messaging.FirebaseMessaging
import com.labters.lottiealertdialoglibrary.ClickListener
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    lateinit var btn_kembali: ImageView
    lateinit var btn_register: CardView
    lateinit var l_email: TextInputLayout
    lateinit var l_password: TextInputLayout
    lateinit var l_nama: TextInputLayout
    lateinit var l_konfir_pass: TextInputLayout
    lateinit var e_email: TextInputEditText
    lateinit var e_nama: TextInputEditText
    lateinit var e_password: TextInputEditText
    lateinit var e_konfir_pass: TextInputEditText
    lateinit var e_phone: TextInputEditText
    lateinit var l_phone: TextInputLayout
    lateinit var nama: String
    lateinit var email: String
    lateinit var phone: String
    lateinit var password: String
    lateinit var progres: ProgressBar
    lateinit var fcmString: String
    lateinit var txt_register: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        getFCM()
        setInit()
        setButton()
        cekvalidasi()
    }

    private fun getFCM() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("Response", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            val token = task.result
            fcmString = token.toString()
            Log.d("Response fcm", token.toString())
        })
    }

    private fun setButton() {
        btn_kembali.setOnClickListener {
            super.onBackPressed()
        }

        btn_register.setOnClickListener {
            if (validasi()) {
                register()
            }
        }
    }

    private fun register() {
        nama = e_nama.text.toString()
        email = e_email.text.toString()
        phone = e_phone.text.toString()
        password = e_password.text.toString()

        progres.visibility = View.VISIBLE
        txt_register.visibility = View.GONE
        ApiConfig.instanceRetrofit.register(nama, email, phone, password, fcmString)
            .enqueue(object : Callback<ResponsModel> {
                override fun onResponse(
                    call: Call<ResponsModel>,
                    response: Response<ResponsModel>
                ) {
                    progres.visibility = View.GONE
                    txt_register.visibility = View.VISIBLE
                    val respon = response.body()!!
                    if (respon.success == 1) {
                        sukses("Anda telah berhasil registrasi, klik ok tombol untuk login!")
                    } else {
                        progres.visibility = View.GONE
                        txt_register.visibility = View.VISIBLE
                        setError(respon.message)
                    }
                }

                override fun onFailure(call: Call<ResponsModel>, t: Throwable) {
                    progres.visibility = View.GONE
                    txt_register.visibility = View.VISIBLE
                    Log.d("Respon", "Pesan: " + t.message)
                    setError(t.message.toString())
                }
            })
    }

    private fun sukses(pesan: String) {
        var alertDialog: LottieAlertDialog = LottieAlertDialog.Builder(this, DialogTypes.TYPE_SUCCESS)
            .setTitle("Sukses")
            .setDescription(pesan)
            .setPositiveText("Ok")
            .setPositiveListener(object : ClickListener {
                override fun onClick(dialog: LottieAlertDialog) {
                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
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
        var alertDialog : LottieAlertDialog = LottieAlertDialog.Builder(this, DialogTypes.TYPE_ERROR)
            .setTitle("Something error")
            .setDescription(pesan)
            .setPositiveText("Oke")
            .setPositiveTextColor(Color.WHITE)
            .setPositiveButtonColor(Color.RED)
            .setPositiveListener(object :ClickListener{
                override fun onClick(dialog: LottieAlertDialog) {
                    dialog.dismiss()
                }

            })
            .build()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun setInit() {
        btn_kembali = findViewById(R.id.btn_kembali)
        btn_register = findViewById(R.id.btn_register)
        e_nama = findViewById(R.id.e_name)
        e_email = findViewById(R.id.e_email)
        e_password = findViewById(R.id.e_password)
        e_konfir_pass = findViewById(R.id.e_konf_password)
        l_nama = findViewById(R.id.l_name)
        l_email = findViewById(R.id.l_email)
        l_password = findViewById(R.id.l_password)
        l_konfir_pass = findViewById(R.id.l_konf_password)
        l_phone = findViewById(R.id.l_phone)
        e_phone = findViewById(R.id.e_phone)
        progres = findViewById(R.id.progress)
        txt_register = findViewById(R.id.txt_register)
    }

    private fun cekvalidasi() {
        e_nama.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (e_nama.text.toString().isEmpty()) {
                    l_nama.isErrorEnabled = false
                } else if (e_nama.text.toString().isNotEmpty()) {
                    l_nama.isErrorEnabled = false
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

        e_konfir_pass.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (e_konfir_pass.text.toString().isEmpty()) {
                    l_konfir_pass.isErrorEnabled = false
                } else if (e_konfir_pass.text.toString().length > 7) {
                    l_konfir_pass.isErrorEnabled = false
                } else if (e_konfir_pass.text.toString()
                        .matches(e_password.text.toString().toRegex())
                ) {
                    l_konfir_pass.isErrorEnabled = false
                } else if (e_konfir_pass.text.toString().isNotEmpty()) {
                    l_konfir_pass.isErrorEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun validasi(): Boolean {
        if (e_nama.text.toString().isEmpty()) {
            l_nama.isErrorEnabled = true
            l_nama.error = "Kolom nama tidak boleh kosong!"
            e_nama.requestFocus()
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
        if (e_konfir_pass.text.toString().isEmpty()) {
            l_konfir_pass.isErrorEnabled = true
            l_konfir_pass.error = "Kolom konfirmasi password tidak boleh kosong!"
            e_konfir_pass.requestFocus()
            return false
        } else if (e_konfir_pass.text.toString().length < 6) {
            l_konfir_pass.isErrorEnabled = true
            l_konfir_pass.error = "Konfirmasi password tidak boleh kurang dari 6 karakter!"
            e_konfir_pass.requestFocus()
            return false
        } else if (!e_konfir_pass.text.toString().matches(e_password.text.toString().toRegex())) {
            l_konfir_pass.isErrorEnabled = true
            l_konfir_pass.error = "Konfirmasi password tidak sama dengan password!"
            e_konfir_pass.requestFocus()
            return false
        }
        return true
    }
}