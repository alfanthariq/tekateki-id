package com.alfanthariq.tekteksil.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by alfanthariq on 30/01/2018.
 */

public class RankingResponse {
    @SerializedName("status")
    private boolean status;
    @SerializedName("data")
    private List<RankingDetail> data;

    public boolean isStatus() {
        return status;
    }

    public List<RankingDetail> getData() {
        return data;
    }
}
