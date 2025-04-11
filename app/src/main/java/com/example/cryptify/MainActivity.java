package com.example.cryptify;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private View encryptButton;
    private View decryptButton;
    private View rsaKeysButton;
    private View rsaEncryptionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Vérifier la connexion au démarrage
        OfflineActivity.checkAndShowOffline(this);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        encryptButton = findViewById(R.id.encryptButton);
        decryptButton = findViewById(R.id.decryptButton);
        rsaKeysButton = findViewById(R.id.rsaKeysButton);
        rsaEncryptionButton = findViewById(R.id.rsaEncryptionButton);
    }

    private void setupClickListeners() {
        encryptButton.setOnClickListener(v -> {
            if (OfflineActivity.checkNetworkConnection(this)) {
                Intent intent = new Intent(MainActivity.this, EncryptActivity.class);
                startActivity(intent);
            } else {
                OfflineActivity.checkAndShowOffline(this);
            }
        });

        decryptButton.setOnClickListener(v -> {
            if (OfflineActivity.checkNetworkConnection(this)) {
                Intent intent = new Intent(MainActivity.this, DecryptActivity.class);
                startActivity(intent);
            } else {
                OfflineActivity.checkAndShowOffline(this);
            }
        });

        rsaKeysButton.setOnClickListener(v -> {
            if (OfflineActivity.checkNetworkConnection(this)) {
                Intent intent = new Intent(MainActivity.this, RsaKeysActivity.class);
                intent.putExtra("username",getIntent().getStringExtra("username"));
                startActivity(intent);
            } else {
                OfflineActivity.checkAndShowOffline(this);
            }
        });

        rsaEncryptionButton.setOnClickListener(v -> {
            if (OfflineActivity.checkNetworkConnection(this)) {
                Intent intent = new Intent(MainActivity.this, RsaEncryptionActivity.class);
                startActivity(intent);
            } else {
                OfflineActivity.checkAndShowOffline(this);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Vérifier la connexion à chaque retour sur l'activité
        OfflineActivity.checkAndShowOffline(this);
    }

    @Override
    public void onBackPressed() {
        // Déconnexion et retour à l'écran de connexion
        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}