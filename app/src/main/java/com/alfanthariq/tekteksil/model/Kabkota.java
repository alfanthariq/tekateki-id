package com.alfanthariq.tekteksil.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alfanthariq on 23/01/2018.
 */

public class Kabkota {
    @SerializedName("id")
    private int id;
    @SerializedName("id_prov")
    private int id_prov;
    @SerializedName("nama")
    private String nama;

    public Kabkota(int id, int id_prov, String nama) {
        this.id = id;
        this.nama = nama;
        this.id_prov = id_prov;
    }

    public int getId() {
        return id;
    }

    public int getId_prov() {
        return id_prov;
    }

    public String getNama() {
        return nama;
    }
}
