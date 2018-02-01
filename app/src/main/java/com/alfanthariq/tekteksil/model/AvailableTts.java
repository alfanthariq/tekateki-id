package com.alfanthariq.tekteksil.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alfanthariq on 16/01/2018.
 */

public class AvailableTts {
    @SerializedName("id_tts")
    private int idTts;
    @SerializedName("edisi_int")
    private int edisiInt;
    @SerializedName("edisi_str")
    private String edisiStr;
    @SerializedName("tgl_terbit")
    private String tglTerbit;
    @SerializedName("nama_file")
    private String namaFile;

    public AvailableTts(int idTts, int edisiInt, String edisiStr, String tglTerbit, String namaFile) {
        this.idTts = idTts;
        this.edisiInt  = edisiInt;
        this.edisiStr = edisiStr;
        this.tglTerbit = tglTerbit;
        this.namaFile = namaFile;
    }

    public int getIdTts() {
        return idTts;
    }

    public void setIdTts(int idTts) {
        this.idTts = idTts;
    }

    public int getEdisiInt() {
        return edisiInt;
    }

    public void setEdisiInt(int edisiInt) {
        this.edisiInt = edisiInt;
    }

    public String getEdisiStr() {
        return edisiStr;
    }

    public void setEdisiStr(String edisiStr) {
        this.edisiStr = edisiStr;
    }

    public String getTglTerbit() {
        return tglTerbit;
    }

    public void setTglTerbit(String tglTerbit) {
        this.tglTerbit = tglTerbit;
    }

    public String getNamaFile() {
        return namaFile;
    }

    public void setNamaFile(String namaFile) {
        this.namaFile = namaFile;
    }
}
