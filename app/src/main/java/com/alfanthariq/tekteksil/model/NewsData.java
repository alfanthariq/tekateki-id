package com.alfanthariq.tekteksil.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alfanthariq on 02/02/2018.
 */

public class NewsData {
    @SerializedName("isi")
    private String isi;

    public NewsData(String isi){
        this.isi = isi;
    }

    public String getIsi() {
        return isi;
    }
}
