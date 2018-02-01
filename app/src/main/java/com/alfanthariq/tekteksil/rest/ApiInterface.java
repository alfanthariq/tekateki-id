package com.alfanthariq.tekteksil.rest;

import com.alfanthariq.tekteksil.model.AvailableTtsResponse;
import com.alfanthariq.tekteksil.model.KabkotaResponse;
import com.alfanthariq.tekteksil.model.LoginResponse;
import com.alfanthariq.tekteksil.model.ProvinsiResponse;
import com.alfanthariq.tekteksil.model.GlobalResponse;
import com.alfanthariq.tekteksil.model.RankingResponse;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by alfanthariq on 16/01/2018.
 */

public interface ApiInterface {
    String BASE_URL = "https://api.alfanthariq.com/tts/public/";

    @GET("available/{tglTerbit}")
    Call<AvailableTtsResponse> getAvailableTTS(@Path("tglTerbit") String tglTerbit);

    @GET("provinsi/")
    Call<ProvinsiResponse> getProvinsi();

    @GET("kabkota/{id_provinsi}")
    Call<KabkotaResponse> getKabkota(@Path("id_provinsi") int id_prov);

    @GET("rank/{start_idx}/{limit}")
    Call<RankingResponse> getRank(@Path("start_idx") int start_idx, @Path("limit") int limit);

    @FormUrlEncoded
    @POST("login/")
    Call<LoginResponse> login(@Field("auth_type") int auth_type, @Field("email") String email,
                              @Field("user_id") String user_id, @Field("password") String password,
                              @Field("full_name") String full_name);

    @FormUrlEncoded
    @POST("register/")
    Call<GlobalResponse> reg(@Field("auth_type") int auth_type, @Field("email") String email,
                             @Field("password_hash") String password, @Field("user_id") String user_id,
                             @Field("full_name") String full_name, @Field("id_kabkota") int id_kabkota);

    @FormUrlEncoded
    @POST("submit_score/")
    Call<GlobalResponse> submit_score(@Field("id_tts") int id_tts, @Field("email") String email,
                             @Field("skor") int skor);

    @FormUrlEncoded
    @POST("forgot/")
    Call<GlobalResponse> send_forgot(@Field("email") String email,
                                     @Field("new_pwd") String password_hash);

    Retrofit retrofit =
            new Retrofit.Builder()
                    .baseUrl(BASE_URL) // REMEMBER TO END with /
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
}
