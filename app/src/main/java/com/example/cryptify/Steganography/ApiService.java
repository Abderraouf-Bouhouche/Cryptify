package com.example.cryptify.Steganography;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.*;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    @GET("generate_key")
    Call<ResponseBody> generateKey();

    @GET("generate_iv")
    Call<ResponseBody> generateIv();

    @POST("encrypt")
    Call<ResponseBody> encrypt(
            @Part MultipartBody.Part image,
            @Part("key")RequestBody key,
            @Part("iv")RequestBody iv,
            @Part("message")RequestBody message
            );

    @POST("decrypt")
    Call<ResponseBody> decrypt();

}
