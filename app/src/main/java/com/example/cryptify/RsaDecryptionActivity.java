package com.example.cryptify;


import static com.example.cryptify.Functions.decryptRsa;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RsaDecryptionActivity extends AppCompatActivity {
    private ImageButton backButton;
    private EditText publicKeyInput;
    private EditText messageInput;
    private Button decryptButton;
    private View blurView;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rsa_decryption);

        try {
            initializeViews();
            setupClickListeners();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog("Error", "Failed to initialize decryption");
            finish();
        }
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        messageInput = findViewById(R.id.messageInput);
        decryptButton= findViewById(R.id.decryptButton);
        blurView = findViewById(R.id.blurView);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
        decryptButton.setOnClickListener(v -> handleDecryption());
    }

    private void handleDecryption() {
        String message = messageInput.getText().toString().trim();

        if ( message.isEmpty()) {
            showErrorDialog("incomplete", "please fill all fields");
            return;
        }

        showLoadingDialog();

        try {
            String decryptedMessage=decryptRsa(message, getIntent().getStringExtra("username"),this);
            hideLoadingDialog();
            Intent intent=new Intent(RsaDecryptionActivity.this,RsaDecryptionResultActivity.class);
            intent.putExtra("decryptedMessage",decryptedMessage);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            hideLoadingDialog();
            showErrorDialog("Decryption failed", "this message is not meant for you");
        }
    }

    private void showBlurView() {
        if (blurView != null) {
            blurView.setVisibility(View.VISIBLE);
            AlphaAnimation animation = new AlphaAnimation(0f, 1f);
            animation.setDuration(300);
            blurView.startAnimation(animation);
        }
    }

    private void hideBlurView() {
        if (blurView != null) {
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

        icon.setImageResource(R.drawable.rsa_encrypt);
        titleText.setVisibility(View.GONE);
        messageText.setText("Encrypting with RSA...");
        btnOk.setVisibility(View.GONE);

        loadingDialog.show();
    }

    private void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
            hideBlurView();
        }
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

        icon.setImageResource(R.drawable.rsa_encrypt);
        titleText.setText("message encrypted");
        messageText.setText("Successfully");
        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            hideBlurView();
            finish();
        });

        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideLoadingDialog();
    }
}