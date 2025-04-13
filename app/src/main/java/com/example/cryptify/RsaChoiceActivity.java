package com.example.cryptify;

import static com.example.cryptify.Functions.getPublicKey;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
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
        OfflineActivity.checkAndShowOffline(this);

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
            if (OfflineActivity.checkNetworkConnection(this)) {
                String username = getIntent().getStringExtra("username");
                if (username == null) {
                    showErrorDialog("Error", "User information not found");
                    return;
                }
                
                String publicKey = getPublicKey(username, this);
                if (publicKey == null) {
                    showErrorDialog("Error", "Failed to load RSA key");
                    return;
                }

                Intent intent = new Intent(RsaChoiceActivity.this, RsaEncryptionActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("publicKey", publicKey);
                startActivity(intent);
            } else {
                OfflineActivity.checkAndShowOffline(this);
            }
        });

        decryptButton.setOnClickListener(v -> {
            if (OfflineActivity.checkNetworkConnection(this)) {
                Intent intent = new Intent(RsaChoiceActivity.this, DecryptActivity.class);
                startActivity(intent);
            } else {
                OfflineActivity.checkAndShowOffline(this);
            }
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
        OfflineActivity.checkAndShowOffline(this);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}