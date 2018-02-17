package com.alfanthariq.tts.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by alfanthariq on 16/01/2018.
 */

public class AvailableTtsResponse {

    @SerializedName("status")
    private boolean status;
    @SerializedName("data")
    private List<AvailableTts> data;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<AvailableTts> getData() {
        return data;
    }

    public void setData(List<AvailableTts> data) {
        this.data = data;
    }
}
