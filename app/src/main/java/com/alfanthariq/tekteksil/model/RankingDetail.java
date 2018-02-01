package com.alfanthariq.tekteksil.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alfanthariq on 30/01/2018.
 */

public class RankingDetail {
    @SerializedName("rank")
    private int rank;
    @SerializedName("total_skor")
    private int total_skor;
    @SerializedName("user_id")
    private String user_id;
    @SerializedName("full_name")
    private String full_name;
    @SerializedName("kota")
    private String kota;
    @SerializedName("prov")
    private String prov;

    public RankingDetail(int rank, int total_skor, String user_id, String full_name, String kota, String prov){
        this.rank = rank;
        this.total_skor = total_skor;
        this.user_id = user_id;
        this.full_name = full_name;
        this.kota = kota;
        this.prov = prov;
    }

    public int getRank() {
        return rank;
    }

    public int getTotal_skor() {
        return total_skor;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getFull_name() {
        return full_name;
    }

    public String getKota() {
        return kota;
    }

    public String getProv() {
        return prov;
    }
}
