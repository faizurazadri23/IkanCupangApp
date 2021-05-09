package com.faizurazadri.ikancupangapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Fish implements Parcelable {

    @SerializedName("id_ikan")
    private int id_ikan;

    @SerializedName("jenis_ikan")
    private String jenis_ikan;

    @SerializedName("harga")
    private int harga;

    @SerializedName("penjual")
    private String penjual;

    @SerializedName("tanggal_beli")
    private String tanggal_beli;

    @SerializedName("image_ikan")
    private String image_ikan;

    protected Fish(Parcel in) {
        id_ikan = in.readInt();
        jenis_ikan = in.readString();
        harga = in.readInt();
        penjual = in.readString();
        tanggal_beli = in.readString();
        image_ikan = in.readString();
    }

    public static final Creator<Fish> CREATOR = new Creator<Fish>() {
        @Override
        public Fish createFromParcel(Parcel in) {
            return new Fish(in);
        }

        @Override
        public Fish[] newArray(int size) {
            return new Fish[size];
        }
    };

    public int getId_ikan() {
        return id_ikan;
    }

    public void setId_ikan(int id_ikan) {
        this.id_ikan = id_ikan;
    }

    public String getJenis_ikan() {
        return jenis_ikan;
    }

    public void setJenis_ikan(String jenis_ikan) {
        this.jenis_ikan = jenis_ikan;
    }

    public int getHarga() {
        return harga;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }

    public String getPenjual() {
        return penjual;
    }

    public void setPenjual(String penjual) {
        this.penjual = penjual;
    }

    public String getTanggal_beli() {
        return tanggal_beli;
    }

    public void setTanggal_beli(String tanggal_beli) {
        this.tanggal_beli = tanggal_beli;
    }

    public String getImage_ikan() {
        return image_ikan;
    }

    public void setImage_ikan(String image_ikan) {
        this.image_ikan = image_ikan;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id_ikan);
        dest.writeString(jenis_ikan);
        dest.writeInt(harga);
        dest.writeString(penjual);
        dest.writeString(tanggal_beli);
        dest.writeString(image_ikan);
    }
}
