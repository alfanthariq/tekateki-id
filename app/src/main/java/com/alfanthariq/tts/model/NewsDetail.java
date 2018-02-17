package com.alfanthariq.tts.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alfanthariq on 02/02/2018.
 */

public class NewsDetail {
    @SerializedName("id")
    private int id;
    @SerializedName("judul")
    private String judul;
    @SerializedName("ctn")
    private String ctn;
    @SerializedName("img")
    private String img64;
    @SerializedName("publish_date")
    private String publish_date;

    public NewsDetail(int id, String judul, String ctn, String img64, String publish_date){
        this.id = id;
        this.judul = judul;
        this.ctn = ctn;
        this.img64 = img64;
        this.publish_date = publish_date;
    }

    public int getId() {
        return id;
    }

    public String getJudul() {
        return judul;
    }

    public String getCtn() {
        return ctn;
    }

    public String getImg64() {
        return img64;
    }

    public String getPublish_date() {
        return publish_date;
    }
}
