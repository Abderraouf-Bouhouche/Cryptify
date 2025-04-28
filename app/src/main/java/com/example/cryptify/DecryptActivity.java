package com.example.cryptify;

import static com.example.cryptify.Helper.saveBitmapToMediaGallery;
import static com.example.cryptify.Helper.uriToFile;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import com.example.cryptify.Steganography.LSBSteganography;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class DecryptActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_FILE_REQUEST = 2;
    private static final int MIN_KEY_LENGTH = 16;
    private static final Pattern KEY_PATTERN = Pattern.compile("^.{" + MIN_KEY_LENGTH + ",}$");

    private ImageButton backButton;
    private LinearLayout imageSelectionContainer;
    private ImageButton clearImageButton;
    private EditText key1Input, key2Input;
    private Button decryptButton;
    private ImageButton folderButton;
    private ImageView imageShow;
    private View blurView;
    private Uri selectedImageUri;
    private Dialog loadingDialog;

    private ExecutorService executorService;
    private Handler mainHandler;
    private String key1,key2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decrypt);
        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        imageSelectionContainer = findViewById(R.id.imageSelectionContainer);
        clearImageButton = findViewById(R.id.clearImageButton);
        key1Input = findViewById(R.id.key1Input);
        key2Input = findViewById(R.id.key2Input);
        decryptButton = findViewById(R.id.decryptButton);
        folderButton = findViewById(R.id.folderButton);
        blurView = findViewById(R.id.blurView);
        imageShow=findViewById(R.id.imageShow);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
        imageSelectionContainer.setOnClickListener(v -> openImagePicker());
        clearImageButton.setOnClickListener(v -> clearSelectedImage());
        decryptButton.setOnClickListener(v -> handleDecryption());
        folderButton.setOnClickListener(v->{
            Intent intent=new Intent(DecryptActivity.this,DecryptListActivity.class);
            intent.putExtra("username",getIntent().getStringExtra("username"));
            startActivity(intent);
        });
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


    private void handleDecryption() {
        String key1 = key1Input.getText().toString().trim();
        String key2 = key2Input.getText().toString().trim();



        if(selectedImageUri==null){
           showErrorDialog("incomplete","please choose a picture");
        }
        if (key1.isEmpty() || key2.isEmpty()) {
            showErrorDialog("incomplete", "please fill in all the keys");
            return;
        }
        File image;
        try {
            image = uriToFile(this, selectedImageUri);
        } catch (IOException e) {
            showErrorDialog("Error", "Error converting URI to file: " + e.getMessage());
            // Optionally, you might want to stop further execution here
            return;
        }
        key1= Base64.getEncoder().encodeToString(key1.getBytes(StandardCharsets.UTF_8));
        key2= Base64.getEncoder().encodeToString(key2.getBytes(StandardCharsets.UTF_8));

        showLoadingDialog();

        final String key1Final=key1;
        final String key2Final=key2;
        executorService= Executors.newSingleThreadExecutor();
        mainHandler= new Handler(Looper.getMainLooper());
        executorService.submit(()->{
            Bitmap bitImage= BitmapFactory.decodeFile(image.getAbsolutePath());
            String decryptedMessage=LSBSteganography.extractMessage(bitImage,key1Final,key2Final);
            mainHandler.post(()->{
                hideLoadingDialog();
                Intent intent=new Intent(DecryptActivity.this, DecryptActivityResult.class);
                intent.putExtra("message",decryptedMessage);
                startActivity(intent);
                finish();
            });
        });
        // TODO: Implémenter la logique de déchiffrement

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

        loadingDialog = new Dialog(this);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.setContentView(R.layout.custom_error_dialog);
        loadingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        loadingDialog.setCancelable(false);

        ImageView icon = loadingDialog.findViewById(R.id.dialogIcon);
        TextView titleText = loadingDialog.findViewById(R.id.dialogTitle);
        TextView messageText = loadingDialog.findViewById(R.id.dialogMessage);
        Button btnOk = loadingDialog.findViewById(R.id.btnOk);

        icon.setImageResource(R.drawable.password);
        titleText.setVisibility(View.GONE);
        messageText.setText("Decrypting...");
        btnOk.setVisibility(View.GONE);

        loadingDialog.show();
    }
    private void hideLoadingDialog(){
        hideBlurView();
        loadingDialog.dismiss();
    }
}