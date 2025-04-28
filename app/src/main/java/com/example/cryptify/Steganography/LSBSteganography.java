package com.example.cryptify.Steganography;


import static com.example.cryptify.Steganography.AESCTR.decrypt;
import static com.example.cryptify.Steganography.AESCTR.encrypt;

import android.graphics.Bitmap;

import android.graphics.Color;
import android.util.Log;

import com.example.cryptify.Helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class LSBSteganography {

    private static final String TAG = "LSBSteganography";

    /**
     * Hides a text message within a Bitmap image using LSB steganography.
     *
     * @param image       The Bitmap image to use as the cover.
     * @param message    The text message to hide.
     * @return true if the message was successfully hidden, false otherwise.
     */
    public static Bitmap hideMessage(Bitmap image, String message,String key,String iv) {
        try {
            message= Helper.addMark(message);
            byte[] byteMessage=encrypt(message,key,iv);
            // Convert the message to a binary string
            String binaryMessage = byteToBinary(byteMessage);

            // Check if the message can fit in the image
            if (binaryMessage.length() > image.getWidth() * image.getHeight() * 3) {
                Log.e(TAG, "Message is too long to hide in this image.");
                return null;
            }

            int bitIndex = 0;
            int width = image.getWidth();
            int height = image.getHeight();

            // Create a mutable copy of the bitmap to modify
            Bitmap modifiedImage = image.copy(Bitmap.Config.ARGB_8888, true);


            int[] pixels = new int[width * height];
            modifiedImage.getPixels(pixels, 0, width, 0, 0, width, height);


            for (int i = 0; i < width * height; i++) {
                if (bitIndex < binaryMessage.length()) {
                    int pixel = pixels[i];

                    int alpha = Color.alpha(pixel);
                    int red = Color.red(pixel);
                    int green = Color.green(pixel);
                    int blue = Color.blue(pixel);

                    // Modify the LSB of each color component if there are still message bits to process
                    if (bitIndex < binaryMessage.length()) {
                        red = setLSB(red, binaryMessage.charAt(bitIndex));
                        bitIndex++;
                    }
                    if (bitIndex < binaryMessage.length()) {
                        green = setLSB(green, binaryMessage.charAt(bitIndex));
                        bitIndex++;
                    }
                    if (bitIndex < binaryMessage.length()) {
                        blue = setLSB(blue, binaryMessage.charAt(bitIndex));
                        bitIndex++;
                    }

                    // Recombine the color components and set the new pixel value
                    int newPixel = Color.argb(alpha, red, green, blue);
                    pixels[i] = newPixel;
                }
            }
            modifiedImage.setPixels(pixels, 0, width, 0, 0, width, height);


            // Save the modified image to a file
            return modifiedImage;

        } catch (Exception e) {
            Log.e(TAG, "Error hiding message: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Extracts a hidden text message from a Bitmap image using LSB steganography.
     *
     * @param image The Bitmap image to extract from.
     * @return The extracted text message, or null if an error occurs.
     */
    public static String extractMessage(Bitmap image,String key,String iv) {
        try {
            StringBuilder binaryMessage = new StringBuilder();
            int width = image.getWidth();
            int height = image.getHeight();

            int[] pixels = new int[width * height];
            image.getPixels(pixels, 0, width, 0, 0, width, height);

            for (int i = 0; i < width * height; i++) {
                int pixel = pixels[i];
                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);

                // Extract the LSB from each color component
                binaryMessage.append(red & 1);
                binaryMessage.append(green & 1);
                binaryMessage.append(blue & 1);
            }
            byte[] messageBytes= binaryToByte(binaryMessage.toString());
            messageBytes=decrypt(messageBytes,key,iv);
            String message=new String(messageBytes,StandardCharsets.UTF_8);
            return Helper.removeMark(message);
        } catch (Exception e) {
            Log.e(TAG, "Error extracting message: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Converts a text string to its binary representation.
     *
     * @param text The text string to convert.
     * @return The binary representation of the text as a string of '0' and '1' characters.
     */
    private static String byteToBinary(byte[] text) {
            StringBuilder binary = new StringBuilder();
            for (byte b : text) {
                int val = b;
                for (int i = 0; i < 8; i++) {
                    binary.append((val & 128) == 0 ? 0 : 1); //MSB
                    val <<= 1;
                }
            }
            return binary.toString();
    }

    /**
     * Converts a binary string to its text representation.  Handles UTF-8.
     *
     * @param binary The binary string to convert.
     * @return The text representation of the binary string.
     */
    private static byte[] binaryToByte(String binary) {
        if (binary == null || binary.length() % 8 != 0) {
            return null;// Handle null or invalid input
        }
        byte[] bytes = new byte[binary.length() / 8];
        for (int i = 0; i < binary.length(); i += 8) {
            String byteStr = binary.substring(i, i + 8);
            bytes[i / 8] = (byte) Integer.parseInt(byteStr, 2);
        }
            return bytes;  // Use UTF-8 encoding
    }

    /**
     * Sets the least significant bit of an integer to the specified value.
     *
     * @param value The integer value.
     * @param bit   The bit value to set (either '0' or '1').
     * @return The modified integer with the LSB set.
     */
    private static int setLSB(int value, char bit) {
        if (bit == '0') {
            return value & 0xFE; // Clear the LSB (make it 0)
        } else if (bit == '1') {
            return value | 0x01; // Set the LSB (make it 1)
        } else {
            throw new IllegalArgumentException("Bit must be '0' or '1'");
        }
    }

    /**
     * Saves a Bitmap to a file.
     *
     * @param bitmap The Bitmap to save.
     * @param filePath The path to save the image.
     * @return true if the Bitmap was saved successfully, false otherwise.
     */
    private static boolean saveBitmap(Bitmap bitmap, String filePath) {
        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                Log.e(TAG, "Failed to create directory: " + file.getParentFile());
                return false;
            }
        }
        try (FileOutputStream fos = new FileOutputStream(file)) {
            // Use PNG to avoid quality loss which would destroy the hidden message
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Error saving Bitmap: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


}
