package com.example.cryptify;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;

public class OfflineActivity extends AppCompatActivity {

    private static final int CHECK_INTERVAL = 2000; // 2 secondes
    private Handler handler;
    private Runnable connectionChecker;
    private ImageButton retryButton;
    private ProgressBar loadingSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline);

        // Initialisation des vues
        retryButton = findViewById(R.id.retryButton);
        loadingSpinner = findViewById(R.id.loadingSpinner);

        // Configuration du bouton retry
        retryButton.setOnClickListener(v -> {
            // Cacher le bouton retry et montrer le spinner pendant la vérification
            retryButton.setVisibility(View.GONE);
            loadingSpinner.setVisibility(View.VISIBLE);
            
            // Vérifier la connexion immédiatement
            checkConnection();
        });

        handler = new Handler();
        connectionChecker = new Runnable() {
            @Override
            public void run() {
                if (isNetworkAvailable()) {
                    finish();
                } else {
                    // Si toujours pas de connexion, montrer le bouton retry et cacher le spinner
                    retryButton.setVisibility(View.VISIBLE);
                    loadingSpinner.setVisibility(View.GONE);
                }
            }
        };
    }

    private void checkConnection() {
        // Vérifier la connexion après un court délai pour l'animation
        handler.postDelayed(() -> {
            if (isNetworkAvailable()) {
                finish();
            } else {
                // Si pas de connexion, montrer le bouton retry et cacher le spinner
                retryButton.setVisibility(View.VISIBLE);
                loadingSpinner.setVisibility(View.GONE);
            }
        }, 1500); // Délai de 1.5 secondes pour l'animation
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Vérifier la connexion au retour sur l'activité
        checkConnection();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Nettoyer les callbacks du handler
        handler.removeCallbacks(connectionChecker);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    // Méthode statique pour vérifier la connexion depuis n'importe quelle activité
    public static boolean checkNetworkConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    // Méthode statique pour lancer l'activité hors ligne si nécessaire
    public static void checkAndShowOffline(Context context) {
        if (!checkNetworkConnection(context)) {
            Intent intent = new Intent(context, OfflineActivity.class);
            context.startActivity(intent);
        }
    }
}