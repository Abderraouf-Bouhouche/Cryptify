/*package com.example.cryptify.Steganography;


import com.google.gson.Gson;
import okhttp3.*;

import java.io.File;

public class StegnoAPI {
    OkHttpClient client;
    public StegnoAPI() {
        client=new OkHttpClient();
    }

    public void awakeApi(){
        //creating request
        Request request=new Request.Builder()
                .url("https://steg-api-qjp4.onrender.com/generate_key")
                .build();
        //sending the request and handling the response
        try(Response response=client.newCall(request).execute()){


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }



    //key generator
    public String generateKey(){
        //creating a get request
        Request request=new Request.Builder()
                .url("https://steg-api-qjp4.onrender.com/generate_key")
                .build();
        //sending the request and handling the response
        try(Response response=client.newCall(request).execute()){
            String jsonResponse=response.body().string();
            response.close();
            Gson gson=new Gson();
            KeyResponse keyResponse=gson.fromJson(jsonResponse,KeyResponse.class);
            return keyResponse.getKey();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    //iv generator
    public String generateIv(){
        //creating a get request
        Request request=new Request.Builder()
                .url("https://steg-api-qjp4.onrender.com/generate_iv")
                .build();
        //sending the request and handling the response
        try(Response response=client.newCall(request).execute()){
            String jsonResponse=response.body().string();
            response.close();
            Gson gson=new Gson();
            IvResponse ivResponse=gson.fromJson(jsonResponse,IvResponse.class);
            return ivResponse.getIv();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    //encrypting photos
    public byte[] encrypt(String key, String iv, File image, String message){
        if(key.isEmpty()){
            key=generateKey();
        }
        if(iv.isEmpty()){
            iv=generateIv();
        }
        String mimeType;
        if(image.getAbsolutePath().endsWith(".png")){
            mimeType="image/png";
        }else if(image.getAbsolutePath().endsWith(".jpg")||image.getAbsolutePath().endsWith(".jpeg")){
            mimeType="image/jpeg";
        }else{
            throw new IllegalArgumentException("unsupported image format");
        }
        //creating the request body
        RequestBody requestBody=new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("key",key)
                .addFormDataPart("iv",iv)
                .addFormDataPart("message",message)
                .addFormDataPart("image", image.getName(),
                    RequestBody.create(image,MediaType.parse(mimeType)))
                .build();

        //building request
        Request request=new Request.Builder()
                .url("https://steg-api-qjp4.onrender.com/encrypt")
                .post(requestBody)
                .build();

        //sending request and handling response
        try(Response response=client.newCall(request).execute()){
               byte[] bytesResponse=response.body().bytes();
               response.close();
               return bytesResponse;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }



    //decrypting photos
    public String decrypt(String key,String iv,File image){
        RequestBody requestBody=new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("key",key)
                .addFormDataPart("iv",iv)
                .addFormDataPart("image","image.jpg",
                        RequestBody.create(image,MediaType.parse("image/png")))
                .build();

        //building request
        Request request=new Request.Builder()
                .url("https://steg-api-qjp4.onrender.com/decrypt")
                .post(requestBody)
                .build();

        //sending request and handling response
        try(Response response=client.newCall(request).execute()){
            String jsonResponse=response.body().string();
            response.close();
            Gson gson=new Gson();
            MessageResponse messageResponse=gson.fromJson(jsonResponse,MessageResponse.class); return messageResponse.getMessage();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }



}

 */