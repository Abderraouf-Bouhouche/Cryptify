package com.example.cryptify;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class DecryptKnownActivity extends AppCompatActivity {
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
        String key=getIntent().getStringExtra("key");
        String iv=getIntent().getStringExtra("iv");
        String path=getIntent().getStringExtra("path");
        Bitmap image= BitmapFactory.decodeFile(path);
    }
}
