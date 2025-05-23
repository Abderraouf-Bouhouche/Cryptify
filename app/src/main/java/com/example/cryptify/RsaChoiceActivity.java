package com.example.cryptify;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RsaChoiceActivity extends AppCompatActivity {
    private View encryptButton;
    private View decryptButton;
    private View backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rsa_choice);

        // Vérifier la connexion au démarrage

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        encryptButton = findViewById(R.id.encryptRsaButton);
        decryptButton = findViewById(R.id.decryptRsaButton);
        backButton = findViewById(R.id.backButton);
    }

    private void setupClickListeners() {
        encryptButton.setOnClickListener(v -> {
                String username = getIntent().getStringExtra("username");
                if (username == null) {
                    showErrorDialog("Error", "User information not found");
                    return;
                }
                
                Intent intent = new Intent(RsaChoiceActivity.this, RsaEncryptionActivity.class);
                startActivity(intent);
        });

        decryptButton.setOnClickListener(v -> {
                String username = getIntent().getStringExtra("username");
                if (username == null) {
                    showErrorDialog("Error", "User information not found");
                    return;
                }
                Intent intent = new Intent(RsaChoiceActivity.this, RsaDecryptionActivity.class);
                intent.putExtra("username",username);
                startActivity(intent);
        });

        backButton.setOnClickListener(v -> finish());
    }

    private void showErrorDialog(String title, String message) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_error_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView titleText = dialog.findViewById(R.id.dialogTitle);
        TextView messageText = dialog.findViewById(R.id.dialogMessage);
        Button btnOk = dialog.findViewById(R.id.btnOk);

        titleText.setText(title);
        messageText.setText(message);
        btnOk.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Vérifier la connexion à chaque retour sur l'activité
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}