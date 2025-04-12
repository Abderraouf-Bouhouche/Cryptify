package com.example.cryptify;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
        encryptButton=findViewById(R.id.encryptRsaButton);
        decryptButton=findViewById(R.id.decryptRsaButton);
        backButton=findViewById(R.id.backButton);
    }



    private void setupClickListeners() {
        encryptButton.setOnClickListener(v -> {
            if (OfflineActivity.checkNetworkConnection(this)) {
                Intent intent = new Intent(RsaChoiceActivity.this, RsaEncryptionActivity.class);
                intent.putExtra("username",getIntent().getStringExtra("username"));
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