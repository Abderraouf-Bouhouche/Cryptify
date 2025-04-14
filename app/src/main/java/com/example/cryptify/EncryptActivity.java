package com.example.cryptify;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Toast;
import android.Manifest;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cryptify.Steganography.StegnoAPI;

import java.io.File;
import java.io.OutputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

public class EncryptActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int MIN_KEY_LENGTH = 16;
    private static final Pattern KEY_PATTERN = Pattern.compile("^.{" + MIN_KEY_LENGTH + ",}$");

    private ImageButton backButton;
    private LinearLayout imageSelectionContainer;
    private ImageView selectedImage;
    private ImageButton clearImageButton;
    private EditText key1Input, key2Input, messageInput;
    private Button encryptButton;
    private View blurView;
    private Uri selectedImageUri;
    ImageView imageShow;

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
        clearImageButton = findViewById(R.id.clearImageButton);
        key1Input = findViewById(R.id.key1Input);
        key2Input = findViewById(R.id.key2Input);
        messageInput = findViewById(R.id.messageInput);
        encryptButton = findViewById(R.id.encryptButton);
        blurView = findViewById(R.id.blurView);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
        imageSelectionContainer.setOnClickListener(v -> openImagePicker());
        clearImageButton.setOnClickListener(v -> clearSelectedImage());
        encryptButton.setOnClickListener(v -> handleEncryption());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void clearSelectedImage() {
        selectedImageUri = null;
        imageShow.setImageURI(null);
        clearImageButton.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                imageShow =findViewById(R.id.imageShow);
                imageShow.setImageURI(selectedImageUri);
                clearImageButton.setVisibility(View.VISIBLE);
                // Vérifier le format de l'image
                String mimeType = getContentResolver().getType(selectedImageUri);
                if (mimeType != null && !mimeType.startsWith("image/jpeg") && !mimeType.startsWith("image/png")) {
                    showErrorDialog("Non supported image", "format\n\nuse PNG or JPG");
                    clearSelectedImage();
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


        if (message.isEmpty()) {
            showErrorDialog("incomplete", "please write a message");
            return;
        }

        showLoadingDialog();
        // TODO: Implémenter la logique d'encryption
        checkPermission();
        StegnoAPI steg=new StegnoAPI();
        File imageFile=new File(String.valueOf(selectedImageUri));
        byte[] imageBytes=null;
        // WARNING: Only use this on background threads!
        Future<byte[]> future = Executors.newSingleThreadExecutor().submit(() -> {
            return steg.encrypt(key1, key2, imageFile, message);
        });

        try {
            imageBytes = future.get(); // Blocks until result is available
            if(imageBytes!=null) {
                Uri savedUri = saveByteArrayToGallery(this, imageBytes, "MyImage_" + System.currentTimeMillis() + ".png");
                if (savedUri != null) {
                    Toast.makeText(this, "Image saved to Gallery!", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this,"image not saved", Toast.LENGTH_SHORT).show();
            }

            // Use imageBytes here
        } catch (Exception e) {
            e.printStackTrace();
        }



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
        ImageView icon = dialog.findViewById(R.id.dialogIcon);

        icon.setImageResource(R.drawable.image);
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

    public Uri saveByteArrayToGallery(Context context, byte[] byteArray, String fileName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        // For Android 10+ (API 29+), specify the directory
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
        }

        Uri uri = null;
        OutputStream outputStream = null;

        try {
            uri = context.getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
            );

            if (uri != null) {
                outputStream = context.getContentResolver().openOutputStream(uri);
                if (outputStream != null) {
                    outputStream.write(byteArray);
                    return uri;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    public void checkPermission(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        101
                );
            }
        }
    }
}