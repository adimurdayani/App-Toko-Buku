package com.example.tokobuku.core.data.model

class ResponsModel {
    var success = 0
    lateinit var message: String
    var data = Costumer()
    var produk: ArrayList<Produk> = ArrayList()
    var transaksis: ArrayList<Transaksi> = ArrayList()
    var costumer: ArrayList<Costumer> = ArrayList()
//
    var rajaongkir = ModelAlamat()
    var transaksi = Transaksi()
//
    var provinsi: ArrayList<ModelAlamat> = ArrayList()
    var kota_kabupaten: ArrayList<ModelAlamat> = ArrayList()
}
