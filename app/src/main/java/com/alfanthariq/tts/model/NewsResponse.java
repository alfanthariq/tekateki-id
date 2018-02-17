package com.alfanthariq.tts.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by alfanthariq on 02/02/2018.
 */

public class NewsResponse {
    @SerializedName("status")
    private boolean status;
    @SerializedName("data")
    private List<NewsDetail> data;

    public boolean isStatus() {
        return status;
    }

    public List<NewsDetail> getData() {
        return data;
    }
}
