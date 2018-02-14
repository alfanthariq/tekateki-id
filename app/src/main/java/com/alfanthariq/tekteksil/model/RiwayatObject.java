package com.alfanthariq.tekteksil.model;

/**
 * Created by alfanthariq on 12/02/2018.
 */

public class RiwayatObject {
    private String edisiStr, tglTerbit, tglKirim;
    private int skor, id_tts;
    public RiwayatObject(){

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

    public String getTglKirim() {
        return tglKirim;
    }

    public void setTglKirim(String tglKirim) {
        this.tglKirim = tglKirim;
    }

    public int getSkor() {
        return skor;
    }

    public void setSkor(int skor) {
        this.skor = skor;
    }

    public int getId_tts() {
        return id_tts;
    }

    public void setId_tts(int id_tts) {
        this.id_tts = id_tts;
    }
}
