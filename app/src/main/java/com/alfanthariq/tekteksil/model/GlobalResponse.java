package com.alfanthariq.tekteksil.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alfanthariq on 29/01/2018.
 */

public class GlobalResponse {
    @SerializedName("status")
    private boolean status;
    @SerializedName("message")
    private String message;

    public boolean isStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
