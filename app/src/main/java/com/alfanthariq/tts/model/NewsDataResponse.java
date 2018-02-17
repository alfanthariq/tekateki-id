package com.alfanthariq.tts.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by alfanthariq on 02/02/2018.
 */

public class NewsDataResponse {
    @SerializedName("status")
    private boolean status;
    @SerializedName("data")
    private List<NewsData> data;

    public boolean isStatus() {
        return status;
    }

    public List<NewsData> getData() {
        return data;
    }
}
