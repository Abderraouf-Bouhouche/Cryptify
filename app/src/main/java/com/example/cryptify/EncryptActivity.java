package com.example.cryptify;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.regex.Pattern;

public class EncryptActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int MIN_KEY_LENGTH = 16;
    private static final Pattern KEY_PATTERN = Pattern.compile("^.{" + MIN_KEY_LENGTH + ",}$");

    private ImageButton backButton;
    private LinearLayout imageSelectionContainer;
    private ImageView selectedImage;
    private EditText key1Input, key2Input, messageInput;
    private Button encryptButton;
    private View blurView;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        imageSelectionContainer = findViewById(R.id.imageSelectionContainer);
        selectedImage = findViewById(R.id.selectedImage);
        key1Input = findViewById(R.id.key1Input);
        key2Input = findViewById(R.id.key2Input);
        messageInput = findViewById(R.id.messageInput);
        encryptButton = findViewById(R.id.encryptButton);
        blurView = findViewById(R.id.blurView);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        imageSelectionContainer.setOnClickListener(v -> openImagePicker());

        encryptButton.setOnClickListener(v -> handleEncryption());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                selectedImage.setImageURI(selectedImageUri);
                // Vérifier le format de l'image
                String mimeType = getContentResolver().getType(selectedImageUri);
                if (mimeType != null && !mimeType.startsWith("image/jpeg") && !mimeType.startsWith("image/png")) {
                    showErrorDialog("Non supported image", "format\n\nuse PNG or JPG");
                    selectedImage.setImageResource(R.drawable.image);
                    selectedImageUri = null;
                }
            }
        }
    }

    private void handleEncryption() {
        String key1 = key1Input.getText().toString();
        String key2 = key2Input.getText().toString();
        String message = messageInput.getText().toString();

        if (selectedImageUri == null) {
            showErrorDialog("incomplete", "please select an image");
            return;
        }

        if (!isValidKey(key1) || !isValidKey(key2)) {
            showErrorDialog("invalid key", "key must be 16 characters");
            return;
        }

        if (message.isEmpty()) {
            showErrorDialog("incomplete", "please write a message");
            return;
        }

        showLoadingDialog();
        // TODO: Implémenter la logique d'encryption
        // Simuler un délai pour le chargement
        new android.os.Handler().postDelayed(() -> {
            showSuccessDialog();
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

        icon.setImageResource(R.drawable.password);
        titleText.setVisibility(View.GONE);
        messageText.setText("Encrypting...");
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

        icon.setImageResource(R.drawable.password);
        titleText.setText("message encrypted");
        messageText.setText("Successfully");
        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            hideBlurView();
            finish();
        });

        dialog.show();
    }
}