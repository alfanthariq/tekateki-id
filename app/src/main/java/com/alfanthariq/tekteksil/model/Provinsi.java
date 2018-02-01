package com.alfanthariq.tekteksil.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alfanthariq on 23/01/2018.
 */

public class Provinsi {
    @SerializedName("id")
    private int id;
    @SerializedName("nama")
    private String nama;

    public Provinsi(int id, String nama) {
        this.id = id;
        this.nama = nama;
    }

    public int getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }
}
