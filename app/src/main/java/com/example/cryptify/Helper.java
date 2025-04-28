package com.example.cryptify;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Helper {
    private static final String SPECIFIC_STRING = "helloMynameisThatGuyLMNOPXYZ";

    public static String addMark(String originalString) {
        return originalString + SPECIFIC_STRING;
    }

    public static String removeMark(String modifiedString) {
        int index = modifiedString.indexOf(SPECIFIC_STRING);
        if (index != -1) {
            return modifiedString.substring(0, index);
        } else {
            return modifiedString; // Return the original if the specific string isn't found
        }
    }



    public static String saveBitmapToMediaGallery(Context context, Bitmap bitmap) {
        /*
        // Set up metadata for the new image
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "image"+System.currentTimeMillis()+".png");
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");

        // Save to the Pictures directory (standard gallery location)
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

        // Get the ContentResolver and insert a new record
        ContentResolver resolver = context.getContentResolver();
        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        try {
            // Write the Bitmap to the output stream
            OutputStream outputStream = resolver.openOutputStream(imageUri);
            if (outputStream != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.close();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "image"+System.currentTimeMillis()+".png");
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");

        // Save to the Pictures directory (standard gallery location)
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
        File directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES); // Or use getExternalFilesDir()
        File file = new File(directory, "MY_image"+System.currentTimeMillis());
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos); // Or use JPEG
            fos.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Or handle the error appropriately
        }
    }

    public static File uriToFile(Context context, Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        File tempFile = File.createTempFile("image", ".jpeg", context.getCacheDir());
        OutputStream outputStream = new FileOutputStream(tempFile);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }

        outputStream.close();
        inputStream.close();

        return tempFile;
    }


    public static byte[] fileToBytes(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        byte[] bytes = new byte[(int) file.length()];
        fis.read(bytes);
        fis.close();
        return bytes;
    }


}
