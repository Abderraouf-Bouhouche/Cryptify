package com.example.cryptify;

import static android.widget.Toast.LENGTH_LONG;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cryptify.Steganography.LSBSteganography;

import java.io.IOException;
import java.io.InputStream;
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
        handleDecryption();
    }
    private void initializeViews(){

    }
    private void setupClickListeners(){

    }
    private void handleDecryption(){

        executorService= Executors.newSingleThreadExecutor();
        mainHandler= new Handler(Looper.getMainLooper());
        executorService.submit(()->{
            String key=getIntent().getStringExtra("key1");
            String iv=getIntent().getStringExtra("key2");
            String path=getIntent().getStringExtra("path");
            try {
                ContentResolver resolver = this.getContentResolver();
                Uri imageUri = Uri.parse(path);
                InputStream inputStream = resolver.openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                if (inputStream != null) {
                    inputStream.close();
                }
                String decryptedMessage= LSBSteganography.extractMessage(bitmap,key,iv);
                mainHandler.post(()->{
                    Intent intent=new Intent(DecryptKnownActivity.this, DecryptActivityResult.class);
                    intent.putExtra("message",decryptedMessage);
                    startActivity(intent);
                    finish();
                });
            } catch (IOException e) {
                Toast.makeText(this,"problem",LENGTH_LONG);
            }

        });
    }
}
