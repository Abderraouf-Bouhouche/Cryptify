package com.example.cryptify;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cryptify.Steganography.LSBSteganography;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DecryptKnownActivity extends AppCompatActivity {
    Handler mainHandler;
    ExecutorService executorService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decrypt_known);
        initializeViews();
        setupClickListeners();
    }
    private void initializeViews(){

    }
    private void setupClickListeners(){

    }
    private void handleDecryption(){

        executorService= Executors.newSingleThreadExecutor();
        mainHandler= new Handler(Looper.getMainLooper());
        executorService.submit(()->{
            String key=getIntent().getStringExtra("key");
            String iv=getIntent().getStringExtra("iv");
            String path=getIntent().getStringExtra("path");
            Bitmap image= BitmapFactory.decodeFile(path);
            String decryptedMessage= LSBSteganography.extractMessage(image,key,iv);
            mainHandler.post(()->{
                Intent intent=new Intent(DecryptKnownActivity.this, DecryptActivityResult.class);
                intent.putExtra("message",decryptedMessage);
                startActivity(intent);
                finish();
            });
        });
    }
}
