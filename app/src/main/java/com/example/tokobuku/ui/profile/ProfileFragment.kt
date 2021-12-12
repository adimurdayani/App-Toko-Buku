package com.example.tokobuku.ui.profile

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.tokobuku.*
import com.example.tokobuku.core.data.adapter.AdapterSliderView
import com.example.tokobuku.core.data.model.Produk
import com.example.tokobuku.ui.auth.LoginActivity
import com.example.tokobuku.util.SharedPref
import com.facebook.shimmer.ShimmerFrameLayout
import com.labters.lottiealertdialoglibrary.ClickListener
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog
import com.smarteist.autoimageslider.SliderView

class ProfileFragment : Fragment() {
    lateinit var btn_logout: TextView
    lateinit var s: SharedPref
    lateinit var tv_nama: TextView
    lateinit var tv_email: TextView
    lateinit var tv_phone: TextView
    lateinit var btn_alamat: RelativeLayout
    lateinit var btn_ubahpassword: RelativeLayout
    lateinit var btn_ubahprofil: RelativeLayout
    lateinit var btn_tentang: RelativeLayout
    lateinit var btn_bantuan: RelativeLayout
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_profile, container, false)
        setinit(view)
        s = SharedPref(requireActivity())
        setData()
        setButton()
        return view
    }

    private fun setData() {
        if (s.getUser() == null) {
            val intent = Intent(activity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            return
        }
        val user = s.getUser()!!
        tv_nama.setText(user.nama)
        tv_email.text = user.email
        tv_phone.text = user.phone
    }

    private fun setButton() {
        btn_alamat.setOnClickListener {
            startActivity(Intent(requireActivity(), TambahAlamat::class.java))
        }
        btn_ubahpassword.setOnClickListener {
            startActivity(Intent(requireActivity(), UbahPassword::class.java))
        }
        btn_ubahprofil.setOnClickListener {
            startActivity(Intent(requireActivity(), UbahProfile::class.java))
        }
        btn_tentang.setOnClickListener {
            startActivity(Intent(requireActivity(), Tentang::class.java))
        }
        btn_bantuan.setOnClickListener {
            startActivity(Intent(requireActivity(), Bantuan::class.java))
        }
        btn_logout.setOnClickListener {
            logout()
        }
    }

    private fun setinit(view: View) {
        btn_logout = view.findViewById(R.id.btn_logout)
        tv_nama = view.findViewById(R.id.nama)
        tv_email = view.findViewById(R.id.email)
        tv_phone = view.findViewById(R.id.phone)
        btn_alamat = view.findViewById(R.id.btn_alamat)
        btn_ubahpassword = view.findViewById(R.id.btn_ubahpassword)
        btn_ubahprofil = view.findViewById(R.id.btn_ubahprofil)
        btn_tentang = view.findViewById(R.id.btn_tentang)
        btn_bantuan = view.findViewById(R.id.btn_bantuan)
    }

    private fun logout() {
        val alertDialog: LottieAlertDialog =
            LottieAlertDialog.Builder(requireActivity(), DialogTypes.TYPE_WARNING)
                .setTitle("Apakah anda yakin?")
                .setDescription("Ingin logout dari aplikasi!")
                .setPositiveText("Iya")
                .setPositiveTextColor(Color.parseColor("#ffeaea"))
                .setPositiveButtonColor(Color.parseColor("#f44242"))
                .setPositiveListener(object : ClickListener {
                    override fun onClick(dialog: LottieAlertDialog) {
                        s.setStatusLogin(false)
                        val intent = Intent(requireActivity(), HomeActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                        requireActivity().finish()
                        dialog.dismiss()
                    }
                })
                .setNegativeText("Tidak")
                .setNegativeTextColor(Color.parseColor("#ffeaea"))
                .setNegativeButtonColor(Color.parseColor("#EDEDED"))
                .setNegativeListener(object : ClickListener {
                    override fun onClick(dialog: LottieAlertDialog) {
                        dialog.dismiss()
                    }
                })
                .build()
        alertDialog.show()
    }

}