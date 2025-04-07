package com.example.cryptify;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
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
import java.util.regex.Pattern;

public class DecryptActivity extends AppCompatActivity {
    private static final int PICK_FILE_REQUEST = 1;
    private static final int MIN_KEY_LENGTH = 16;
    private static final Pattern KEY_PATTERN = Pattern.compile("^.{" + MIN_KEY_LENGTH + ",}$");

    private ImageButton backButton;
    private EditText encryptedInput;
    private EditText key1Input, key2Input;
    private Button decryptButton;
    private ImageButton folderButton;
    private View blurView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decrypt);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        encryptedInput = findViewById(R.id.encryptedInput);
        key1Input = findViewById(R.id.key1Input);
        key2Input = findViewById(R.id.key2Input);
        decryptButton = findViewById(R.id.decryptButton);
        folderButton = findViewById(R.id.folderButton);
        blurView = findViewById(R.id.blurView);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        folderButton.setOnClickListener(v -> openFilePicker());

        decryptButton.setOnClickListener(v -> handleDecryption());
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/*");
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            // TODO: Lire le contenu du fichier et le mettre dans encryptedInput
            encryptedInput.setText("Contenu chiffré du fichier");
        }
    }

    private void handleDecryption() {
        String encryptedText = encryptedInput.getText().toString().trim();
        String key1 = key1Input.getText().toString().trim();
        String key2 = key2Input.getText().toString().trim();

        if (encryptedText.isEmpty()) {
            showErrorDialog("text to Decrypt", "is empty");
            return;
        }

        if (!isValidKey(key1) || !isValidKey(key2)) {
            showErrorDialog("wrong key", "");
            return;
        }

        showLoadingDialog();
        // TODO: Implémenter la logique de déchiffrement
        // Simuler un délai pour le chargement
        new android.os.Handler().postDelayed(() -> {
            showSuccessDialog("Message déchiffré");
        }, 2000);
    }

    private boolean isValidKey(String key) {
        return KEY_PATTERN.matcher(key).matches();
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

        icon.setImageResource(R.drawable.decrypt);
        titleText.setVisibility(View.GONE);
        messageText.setText("Decrypting...");
        btnOk.setVisibility(View.GONE);

        dialog.show();
    }

    private void showSuccessDialog(String decryptedText) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_error_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ImageView icon = dialog.findViewById(R.id.dialogIcon);
        TextView titleText = dialog.findViewById(R.id.dialogTitle);
        TextView messageText = dialog.findViewById(R.id.dialogMessage);
        Button btnOk = dialog.findViewById(R.id.btnOk);

        icon.setImageResource(R.drawable.decrypt);
        titleText.setText("Decrypted text");
        messageText.setText(decryptedText);
        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            hideBlurView();
        });

        dialog.show();
    }
}