package com.example.cryptify;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DecryptActivityResult  extends AppCompatActivity{

        private View okButton;
        private View copyButton;
        private TextView HiddenMessage;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_decrypt_result);

            initializeViews();
            setupClickListeners();

            HiddenMessage.setText(getIntent().getStringExtra("message"));
        }

        private void initializeViews() {
            okButton=findViewById(R.id.okButton);
            copyButton=findViewById(R.id.copyButton);
            HiddenMessage =findViewById(R.id.hiddenMessage);
        }

        private void setupClickListeners() {
            okButton.setOnClickListener(v -> finish());
            copyButton.setOnClickListener(v ->copyToClipboard("hidden Message", HiddenMessage.getText().toString()));
        }


        private void copyToClipboard(String label, String text) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(label, text);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, label + " copied to clipboard", Toast.LENGTH_SHORT).show();
        }
    }

