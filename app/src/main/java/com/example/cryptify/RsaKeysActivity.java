package com.example.cryptify;

import static com.example.cryptify.Functions.getPublicKey;

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

public class RsaKeysActivity extends AppCompatActivity {
    private ImageButton backButton;
    private TextView publicKeyText;
    private ImageButton copyPublicKeyButton;
    private Button generateButton;
    private View blurView;
    private Dialog loadingDialog;

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
        copyPublicKeyButton = findViewById(R.id.copyPublicKeyButton);
        blurView = findViewById(R.id.blurView);
        loadKeys();
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
        copyPublicKeyButton.setOnClickListener(v -> copyToClipboard("Public Key", publicKeyText.getText().toString()));
    }

    private void loadKeys() {
        showLoadingDialog();
            String username = getIntent().getStringExtra("username");
            String publicKey = getPublicKey(username, this);
            if(publicKey==null){
                showErrorDialog("Oops","failed loading key");
                return;
            }
            hideLoadingDialog();
            publicKeyText.setText(publicKey);
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

        loadingDialog = new Dialog(this);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.setContentView(R.layout.custom_error_dialog);
        loadingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        loadingDialog.setCancelable(false);

        ImageView icon = loadingDialog.findViewById(R.id.dialogIcon);
        TextView titleText = loadingDialog.findViewById(R.id.dialogTitle);
        TextView messageText = loadingDialog.findViewById(R.id.dialogMessage);
        Button btnOk = loadingDialog.findViewById(R.id.btnOk);

        icon.setImageResource(R.drawable.key);
        titleText.setVisibility(View.GONE);
        messageText.setText("Generating RSA keys...");
        btnOk.setVisibility(View.GONE);

        loadingDialog.show();
    }
    private void hideLoadingDialog() {
        loadingDialog.dismiss();
        hideBlurView();
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