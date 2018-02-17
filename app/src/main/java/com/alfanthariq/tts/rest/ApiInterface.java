package com.alfanthariq.tts.rest;

import com.alfanthariq.tts.model.AvailableTtsResponse;
import com.alfanthariq.tts.model.KabkotaResponse;
import com.alfanthariq.tts.model.LoginResponse;
import com.alfanthariq.tts.model.NewsDataResponse;
import com.alfanthariq.tts.model.NewsResponse;
import com.alfanthariq.tts.model.ProvinsiResponse;
import com.alfanthariq.tts.model.GlobalResponse;
import com.alfanthariq.tts.model.RankingResponse;
import com.alfanthariq.tts.model.RiwayatResponse;

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

    @GET("rank/{tipe}/{start_idx}/{limit}")
    Call<RankingResponse> getRank(@Path("tipe") int tipe, @Path("start_idx") int start_idx, @Path("limit") int limit);

    @GET("news-list/{start_idx}/{limit}")
    Call<NewsResponse> getNews(@Path("start_idx") int start_idx, @Path("limit") int limit);

    @GET("news/{id}")
    Call<NewsDataResponse> getNewsByID(@Path("id") int id);

    @GET("submit_status/{id}")
    Call<GlobalResponse> getSubmitStatus(@Path("id") int id);

    @FormUrlEncoded
    @POST("login/")
    Call<LoginResponse> login(@Field("auth_type") int auth_type, @Field("email") String email,
                              @Field("user_id") String user_id, @Field("password") String password,
                              @Field("full_name") String full_name);

    @FormUrlEncoded
    @POST("logout/")
    Call<GlobalResponse> logout(@Field("email") String email);

    @FormUrlEncoded
    @POST("register/")
    Call<GlobalResponse> reg(@Field("auth_type") int auth_type, @Field("email") String email,
                             @Field("password_hash") String password, @Field("user_id") String user_id,
                             @Field("full_name") String full_name, @Field("id_kabkota") int id_kabkota);

    @FormUrlEncoded
    @POST("submit_score/")
    Call<GlobalResponse> submit_score(@Field("id_tts") int id_tts, @Field("email") String email,
                             @Field("skor") int skor, @Field("timezone") String timezone);

    @FormUrlEncoded
    @POST("forgot/")
    Call<GlobalResponse> send_forgot(@Field("email") String email,
                                     @Field("new_pwd") String password_hash);

    @FormUrlEncoded
    @POST("update_profile/")
    Call<GlobalResponse> update_profile(@Field("email") String email, @Field("full_name") String full_name,
                                        @Field("img64") String img64, @Field("id_kabkota") int id_kabkota);

    @FormUrlEncoded
    @POST("check_notif/")
    Call<GlobalResponse> checkNotif(@Field("android_id") String android_id);

    @FormUrlEncoded
    @POST("history_score/")
    Call<RiwayatResponse> getRiwayat(@Field("email") String email, @Field("start_idx") int startIdx,
                                     @Field("limit") int limit);

    Retrofit retrofit =
            new Retrofit.Builder()
                    .baseUrl(BASE_URL) // REMEMBER TO END with /
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
}
