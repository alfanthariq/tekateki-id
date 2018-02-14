package com.alfanthariq.tekteksil.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alfanthariq on 12/02/2018.
 */

public class RiwayatDetail {
    @SerializedName("id_tts")
    private int id_tts;
    @SerializedName("edisi_str")
    private String edisiStr;
    @SerializedName("tgl_terbit")
    private String tglTerbit;
    @SerializedName("submit_date")
    private String tglKirim;
    @SerializedName("skor")
    private int skor;

    public RiwayatDetail(int id_tts, String edisiStr, String tglTerbit, String tglKirim, int skor){
        this.id_tts = id_tts;
        this.edisiStr = edisiStr;
        this.tglTerbit = tglTerbit;
        this.tglKirim = tglKirim;
    }

    public int getId_tts() {
        return id_tts;
    }

    public String getEdisiStr() {
        return edisiStr;
    }

    public String getTglTerbit() {
        return tglTerbit;
    }

    public String getTglKirim() {
        return tglKirim;
    }

    public int getSkor() {
        return skor;
    }
}
