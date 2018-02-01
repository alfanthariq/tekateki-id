package com.alfanthariq.tekteksil.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by alfanthariq on 23/01/2018.
 */

public class ProvinsiResponse {
    @SerializedName("status")
    private boolean status;
    @SerializedName("data")
    private List<Provinsi> data;

    public boolean isStatus() {
        return status;
    }

    public List<Provinsi> getData() {
        return data;
    }
}
