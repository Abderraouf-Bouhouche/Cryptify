package com.example.cryptify;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EncryptActivityResult extends AppCompatActivity {


    private ImageButton backButton;
    private Button okButton;
    private ImageView imageShow;
    private TextView key1Text;
    private TextView key2Text;
    private ImageButton copyKey1;
    private ImageButton copyKey2;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt_result);
        initializeViews();
        setupClickListeners();

        Uri uri=Uri.parse(getIntent().getStringExtra("Uri"));
        imageShow.setImageURI(uri);
        key2Text.setText(getIntent().getStringExtra("key2"));
        key1Text.setText(getIntent().getStringExtra("key1"));
    }

    private void initializeViews(){
        backButton=findViewById(R.id.backButton);
        okButton=findViewById(R.id.okButton);
        imageShow=findViewById(R.id.imageShow);
        key1Text=findViewById(R.id.Key1Text);
        key2Text=findViewById(R.id.Key2Text);
        copyKey1=findViewById(R.id.copyKey1Button);
        copyKey2=findViewById(R.id.copyKey2Button);
    }
    private void setupClickListeners(){
        backButton.setOnClickListener(v -> finish());
        okButton.setOnClickListener(v -> finish());
        copyKey1.setOnClickListener(v->copyToClipboard("key1",getIntent().getStringExtra("key1")));
        copyKey2.setOnClickListener(v->copyToClipboard("key2",getIntent().getStringExtra("key2")));
    }




    private void copyToClipboard(String label, String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, label + " copied to clipboard", Toast.LENGTH_SHORT).show();
    }
   }
