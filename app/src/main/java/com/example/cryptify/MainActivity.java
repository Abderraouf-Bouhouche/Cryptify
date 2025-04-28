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
                Intent intent = new Intent(MainActivity.this, EncryptActivity.class);
                intent.putExtra("username",getIntent().getStringExtra("username"));
                startActivity(intent);
        });

        decryptButton.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, DecryptActivity.class);
                intent.putExtra("username",getIntent().getStringExtra("username"));
                startActivity(intent);
        });

        rsaKeysButton.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, RsaKeysActivity.class);
                intent.putExtra("username", getIntent().getStringExtra("username"));
                startActivity(intent);
        });

        rsaEncryptionButton.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, RsaChoiceActivity.class);
                intent.putExtra("username", getIntent().getStringExtra("username"));
                startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Vérifier la connexion à chaque retour sur l'activité
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