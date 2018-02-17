package com.alfanthariq.tts.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by alfanthariq on 22/01/2018.
 */

public class LoginResponse {
    @SerializedName("status")
    private boolean status;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private List<LoginDetail> data;

    public boolean isStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<LoginDetail> getData() {
        return data;
    }
}
