package com.example.cryptify;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cryptify.RSA.RsaImplementer;

public class RsaEcryptionResultActivity extends AppCompatActivity {
    private View backButton;
    private View okButton;
    private View copyButton;
    private TextView encryptedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rsa_encryption_result);

            initializeViews();
            setupClickListeners();

            encryptedText.setText(getIntent().getStringExtra("encryptedMessage"));
    }

    private void initializeViews() {
        backButton=findViewById(R.id.backButton);
        okButton=findViewById(R.id.okButton);
        copyButton=findViewById(R.id.copyButton);
        encryptedText=findViewById(R.id.encryptedMessage);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
        okButton.setOnClickListener(v -> finish());
        copyButton.setOnClickListener(v ->copyToClipboard("encrypted Message",encryptedText.getText().toString()));
    }


    private void copyToClipboard(String label, String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, label + " copied to clipboard", Toast.LENGTH_SHORT).show();
    }
}
