package com.alfanthariq.tekteksil.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alfanthariq on 22/01/2018.
 */

public class LoginDetail {
    @SerializedName("auth_type")
    private int auth_type;
    @SerializedName("email")
    private String email;
    @SerializedName("password_hash")
    private String password_hash;
    @SerializedName("user_id")
    private String user_id;
    @SerializedName("full_name")
    private String full_name;
    @SerializedName("id_kabkota")
    private int id_kabkota;
    @SerializedName("is_activate")
    private int is_activate;
    @SerializedName("confirm_key")
    private String confirm_key;

    public LoginDetail(int auth_type, String email, String password_hash, String user_id,
                       String full_name, int id_kabkota, int is_activate,String confirm_key) {
        this.auth_type = auth_type;
        this.email = email;
        this.password_hash = password_hash;
        this.user_id = user_id;
        this.full_name = full_name;
        this.id_kabkota = id_kabkota;
        this.is_activate = is_activate;
        this.confirm_key = confirm_key;
    }

    public int getAuth_type() {
        return auth_type;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword_hash() {
        return password_hash;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getFull_name() {
        return full_name;
    }

    public int getId_kabkota() {
        return id_kabkota;
    }

    public int getIs_activate() {
        return is_activate;
    }

    public String getConfirm_key() {
        return confirm_key;
    }
}
