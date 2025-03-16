package com.example.cryptify;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;

public class IntroActivity extends AppCompatActivity {
    private LottieAnimationView animationView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        // Initialisation de l'animation Lottie
        animationView = findViewById(R.id.animationView);
        animationView.setAnimation(R.raw.lock); // Utilisation de l'animation lock.json
        animationView.playAnimation();

        // Crée un délai de 3 secondes
        new Handler().postDelayed(() -> {
            // Crée une intention pour démarrer SignInActivity
            Intent intent = new Intent(IntroActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        }, 3000);
    }
}
