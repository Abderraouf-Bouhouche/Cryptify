package com.example.cryptify;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class DecryptListActivity extends AppCompatActivity {
    private LinearLayout imageListLayout;
    private ImageButton backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decrypt_list);
        initializeViews();
        setupClickListeners();
        fillImages();
    }
    private void fillImages(){
        ArrayList<ImageStructure> imageList=new ArrayList<ImageStructure>();
        try (DatabaseHelper db = new DatabaseHelper(this)) {
            imageList = db.getImagesByUsername(getIntent().getStringExtra("username"));
            for (ImageStructure image:imageList) {
                View imageComponent=createImageComponent(this,image);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, // Or MATCH_PARENT
                        LinearLayout.LayoutParams.MATCH_PARENT// Or MATCH_PARENT
                );

                layoutParams.setMargins(0, 8, 0, 8); // left, top, right, bottom in pixels

                final float scale = this.getResources().getDisplayMetrics().density;
                int topMarginInPixels = (int) (8 * scale + 0.5f); //for top margin
                layoutParams.topMargin = topMarginInPixels;
                imageComponent.setLayoutParams(layoutParams);
                imageListLayout.addView(imageComponent);
            }
        } catch (Exception e) {
            showErrorDialog("error",e.getMessage());
        }
    }
    private void initializeViews(){
        backButton=findViewById(R.id.backButton);
        imageListLayout=findViewById(R.id.image_list);
    }
    private void setupClickListeners(){
        backButton.setOnClickListener(v->finish());
    }

    // Function to create the image component.
    private View createImageComponent(Context context, ImageStructure imageData) {
        // Inflate the XML layout.
        LayoutInflater inflater = LayoutInflater.from(context);
        ConstraintLayout view = (ConstraintLayout) inflater.inflate(R.layout.image_component_view, null); // Explicitly cast to ConstraintLayout

        // Get the views within the inflated layout.
        ImageView imageView = view.findViewById(R.id.image);
        TextView idTextView = view.findViewById(R.id.id);
        TextView dateTextView = view.findViewById(R.id.date);

        // Set the data.
        idTextView.setText("ID: " + imageData.id);
        dateTextView.setText("Date: " + formatDate(imageData.date_added));

        // Load the image from the byte array.
        try {
            ContentResolver resolver = context.getContentResolver();
            Uri imageUri = Uri.parse(imageData.image);
            InputStream inputStream = resolver.openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            imageView.setImageBitmap(bitmap);
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            showErrorDialog("error",e.getMessage());
            imageView.setImageResource(R.drawable.image); // Replace with your error drawable
        }
        imageView.setOnClickListener(v-> {
            Intent intent = new Intent(DecryptListActivity.this, DecryptKnownActivity.class);
            intent.putExtra("key1", imageData.key);
            intent.putExtra("key2", imageData.iv);
            intent.putExtra("path", imageData.image);
            startActivity(intent);
        });
        return view;
    }

    // Helper function to format the date.
    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(date);
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
   }
