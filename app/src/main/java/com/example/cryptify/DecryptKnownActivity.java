package com.example.cryptify;

import static android.widget.Toast.LENGTH_LONG;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cryptify.Steganography.LSBSteganography;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eightbitlab.com.blurview.BlurView;

public class DecryptKnownActivity extends AppCompatActivity {
    Handler mainHandler;
    ExecutorService executorService;
    EditText key1Text,key2Text;
    Button decryptButton;
    ImageView imageView;
    String key,iv,path;
    Dialog loadingDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decrypt_known);

        initializeViews();
        setupClickListeners();
        key=getIntent().getStringExtra("key1");
        iv=getIntent().getStringExtra("key2");
        path=getIntent().getStringExtra("path");
        Uri uri=Uri.parse(path);

        imageView.setImageURI(uri);

        key1Text.setText(key);
        key2Text.setText(iv);
    }

    private void initializeViews(){
        decryptButton=findViewById(R.id.decryptButton);
        imageView=findViewById(R.id.image);
        key1Text=findViewById(R.id.key1Show);
        key2Text=findViewById(R.id.key2Show);
    }
    private void setupClickListeners(){
        decryptButton.setOnClickListener(v->handleDecryption());

    }
    private void handleDecryption(){
        showLoadingDialog();
        executorService= Executors.newSingleThreadExecutor();
        mainHandler= new Handler(Looper.getMainLooper());
        executorService.submit(()->{
            try {
                ContentResolver resolver = this.getContentResolver();
                Uri imageUri = Uri.parse(path);
                InputStream inputStream = resolver.openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                String decryptedMessage= LSBSteganography.extractMessage(bitmap,key,iv);
                mainHandler.post(()->{
                    hideLoadingDialog();
                    Intent intent=new Intent(DecryptKnownActivity.this, DecryptActivityResult.class);
                    intent.putExtra("message",decryptedMessage);
                    startActivity(intent);
                    finish();
                });
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                Toast.makeText(this,"problem",LENGTH_LONG);
            }

        });

    }





    private void showErrorDialog(String title, String message) {

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
        });

        dialog.show();
    }

    private void showLoadingDialog() {

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
        messageText.setText("Encrypting...");
        btnOk.setVisibility(View.GONE);

        loadingDialog.show();
    }
    private void hideLoadingDialog(){
        loadingDialog.dismiss();
    }

}
