package com.example.cryptify.Steganography;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.google.gson.Gson;

public class ApiClient {
    private static final String BASE_URL = "https://steg-api-qjp4.onrender.com/";
    private static Retrofit retrofit = null;

    public static ApiService getApiService() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(90, TimeUnit.SECONDS)
                    .readTimeout(90,TimeUnit.SECONDS)
                    .writeTimeout(90,TimeUnit.SECONDS)
                    .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }

    public static void encrypt(String key1Value, String key2Value, File imageFile, String messageValue,
                                  final ApiCallback<byte[]> callback) {

        // Create request body parts
        RequestBody key= RequestBody.create(key1Value,MediaType.get("text/plain"));
        RequestBody iv= RequestBody.create(key2Value,MediaType.get("text/plain"));
        RequestBody message= RequestBody.create(messageValue,MediaType.get("text/plain"));

        String mimeType= URLConnection.guessContentTypeFromName(imageFile.getName());
        if(mimeType==null){
            mimeType="image/*";
        }

        RequestBody requestFile = RequestBody.create(imageFile,MediaType.get(mimeType));
        MultipartBody.Part image= MultipartBody.Part.createFormData("image", imageFile.getName(), requestFile);
        // Make the API call
        retrofit.create(ApiService.class)
                .encrypt(image,key,iv,message)
                .enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        byte[] imageBytes = response.body().bytes();
                        Log.d("API_SUCCESS", "Received " + imageBytes.length + " bytes");
                        callback.onSuccess(imageBytes);
                    } else {
                        String errorBody = response.errorBody() != null ?
                                response.errorBody().string() : "No error body";
                        Log.e("API_ERROR", "Code: " + response.code() + " - " + errorBody);
                    }
                }catch(IOException e){

                    Log.e("API_ERROR", "IO Error: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("API_FAILURE", "Request failed: " + t.getMessage(), t);
            }
        });
    }

    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onFailure(String error);
    }

    public static void generate_key(final ApiCallback<String> callback){

        Call<ResponseBody> call=getApiService().generateKey();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful() && response.body() != null){
                    String key;
                    try {
                        key=response.body().string();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Gson gson=new Gson();
                    KeyResponse keyResponse=gson.fromJson(key,KeyResponse.class);
                    callback.onSuccess(keyResponse.getKey());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                callback.onFailure("Network error:"+ throwable.getMessage());
            }
        });
    }
    public static void generate_iv(final ApiCallback<String> callback){
        Call<ResponseBody> call=getApiService().generateIv();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful() && response.body() != null){
                    String iv;
                    try {
                        iv=response.body().string();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Gson gson=new Gson();
                    IvResponse ivResponse=gson.fromJson(iv,IvResponse.class);
                    callback.onSuccess(ivResponse.getIv());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                callback.onFailure("Network error:"+ throwable.getMessage());
            }
        });
    }
}
