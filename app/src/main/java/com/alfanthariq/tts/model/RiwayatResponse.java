package com.alfanthariq.tts.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by alfanthariq on 12/02/2018.
 */

public class RiwayatResponse {
    @SerializedName("status")
    private boolean status;
    @SerializedName("data")
    private List<RiwayatDetail> data;

    public boolean isStatus() {
        return status;
    }

    public List<RiwayatDetail> getData() {
        return data;
    }
}
