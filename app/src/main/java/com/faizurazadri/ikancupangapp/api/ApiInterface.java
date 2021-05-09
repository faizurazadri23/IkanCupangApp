package com.faizurazadri.ikancupangapp.api;

import com.faizurazadri.ikancupangapp.model.FishResponse;
import com.faizurazadri.ikancupangapp.model.GetFish;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiInterface {

    @GET("Ikan")
    Call<GetFish> getDataFish();

    @FormUrlEncoded
    @POST("Ikan")
    Call<FishResponse> insertFish(@Field("jenis_ikan") String jenis_ikan,
                                  @Field("harga") int harga,
                                  @Field("penjual") String penjual,
                                  @Field("tanggal_beli") String tanggal_beli,
                                  @Field("image_ikan") String image_ikan);

    @FormUrlEncoded
    @PUT("Ikan/{key}")
    Call<FishResponse> updateFish(@Path("key") String key,
                                  @Field("jenis_ikan") String jenis_ikan,
                                  @Field("harga") int harga,
                                  @Field("penjual") String penjual,
                                  @Field("tanggal_beli") String tanggal_beli,
                                  @Field("image_ikan") String image_ikan);

    @DELETE("Ikan/{key}")
    Call<FishResponse> deleteFish(@Path("key") String key);
}
