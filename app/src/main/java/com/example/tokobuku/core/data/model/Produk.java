package com.example.tokobuku.core.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "keranjang")
public class Produk {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idTb")
    public int idTb;

    public int id;
    public String nama_produk;
    public String harga;
    public String deskripsi;
    public String kategori;
    public String image;
    public String created_at;
    public String updated_at;
    public String stok;
    public String berat;
    public int toko_id;
    public String nama_toko;

    public int jumlah = 1;
    public boolean selected = true;
}
