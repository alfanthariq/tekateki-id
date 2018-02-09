package com.alfanthariq.tekteksil.model;

/**
 * Created by alfanthariq on 30/01/2018.
 */

public class RankingObject {
    private int ranking, total_skor;
    private String user_id, full_name, kota, prov, img64;

    public RankingObject(){

    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public int getTotal_skor() {
        return total_skor;
    }

    public void setTotal_skor(int total_skor) {
        this.total_skor = total_skor;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getKota() {
        return kota;
    }

    public void setKota(String kota) {
        this.kota = kota;
    }

    public String getProv() {
        return prov;
    }

    public void setProv(String prov) {
        this.prov = prov;
    }

    public String getImg64() {
        return img64;
    }

    public void setImg64(String img64) {
        this.img64 = img64;
    }
}
