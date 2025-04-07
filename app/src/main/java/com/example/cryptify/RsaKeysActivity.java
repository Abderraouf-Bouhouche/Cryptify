package com.example.cryptify;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class RsaKeysActivity extends AppCompatActivity {
    private ImageButton backButton;
    private TextView publicKeyText, privateKeyText;
    private ImageButton copyPublicKeyButton, copyPrivateKeyButton;
    private Button generateButton;
    private View blurView;

    private KeyPair currentKeyPair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rsa_keys);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        publicKeyText = findViewById(R.id.publicKeyText);
        privateKeyText = findViewById(R.id.privateKeyText);
        copyPublicKeyButton = findViewById(R.id.copyPublicKeyButton);
        copyPrivateKeyButton = findViewById(R.id.copyPrivateKeyButton);
        generateButton = findViewById(R.id.generateButton);
        blurView = findViewById(R.id.blurView);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        generateButton.setOnClickListener(v -> generateKeys());

        copyPublicKeyButton.setOnClickListener(v -> copyToClipboard("Public Key", publicKeyText.getText().toString()));
        copyPrivateKeyButton
                .setOnClickListener(v -> copyToClipboard("Private Key", privateKeyText.getText().toString()));
    }

    private void generateKeys() {
        showLoadingDialog();

        new Thread(() -> {
            try {
                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
                generator.initialize(2048);
                currentKeyPair = generator.generateKeyPair();

                String publicKey = Base64.getEncoder().encodeToString(currentKeyPair.getPublic().getEncoded());
                String privateKey = Base64.getEncoder().encodeToString(currentKeyPair.getPrivate().getEncoded());

                runOnUiThread(() -> {
                    publicKeyText.setText(publicKey);
                    privateKeyText.setText(privateKey);
                    showSuccessDialog();
                });

            } catch (NoSuchAlgorithmException e) {
                runOnUiThread(() -> {
                    showErrorDialog("Error", "Failed to generate RSA keys");
                });
            }
        }).start();
    }

    private void copyToClipboard(String label, String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, label + " copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    private void showBlurView() {
        blurView.setVisibility(View.VISIBLE);
        AlphaAnimation animation = new AlphaAnimation(0f, 1f);
        animation.setDuration(300);
        blurView.startAnimation(animation);
    }

    private void hideBlurView() {
        AlphaAnimation animation = new AlphaAnimation(1f, 0f);
        animation.setDuration(300);
        animation.setAnimationListener(new android.view.animation.Animation.AnimationListener() {
            @Override
            public void onAnimationStart(android.view.animation.Animation animation) {
            }

            @Override
            public void onAnimationEnd(android.view.animation.Animation animation) {
                blurView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(android.view.animation.Animation animation) {
            }
        });
        blurView.startAnimation(animation);
    }

    private void showErrorDialog(String title, String message) {
        showBlurView();

        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_error_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView titleText = dialog.findViewById(R.id.dialogTitle);
        TextView messageText = dialog.findViewById(R.id.dialogMessage);
        Button btnOk = dialog.findViewById(R.id.btnOk);

        titleText.setText(title);
        messageText.setText(message);
        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            hideBlurView();
        });

        dialog.setOnDismissListener(dialogInterface -> hideBlurView());
        dialog.show();
    }

    private void showLoadingDialog() {
        showBlurView();

        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_error_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false);

        ImageView icon = dialog.findViewById(R.id.dialogIcon);
        TextView titleText = dialog.findViewById(R.id.dialogTitle);
        TextView messageText = dialog.findViewById(R.id.dialogMessage);
        Button btnOk = dialog.findViewById(R.id.btnOk);

        icon.setImageResource(R.drawable.key);
        titleText.setVisibility(View.GONE);
        messageText.setText("Generating RSA keys...");
        btnOk.setVisibility(View.GONE);

        dialog.show();
    }

    private void showSuccessDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_error_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ImageView icon = dialog.findViewById(R.id.dialogIcon);
        TextView titleText = dialog.findViewById(R.id.dialogTitle);
        TextView messageText = dialog.findViewById(R.id.dialogMessage);
        Button btnOk = dialog.findViewById(R.id.btnOk);

        icon.setImageResource(R.drawable.key);
        titleText.setText("RSA keys");
        messageText.setText("Generated Successfully");
        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            hideBlurView();
        });

        dialog.show();
    }
}