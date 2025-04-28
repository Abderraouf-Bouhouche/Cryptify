package com.example.cryptify.Steganography;


import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class AESCTR {

    private static final String ALGORITHM = "AES/CTR/NoPadding"; // Use NoPadding, we handle padding ourselves
    private static final int KEY_SIZE = 256; // Key size in bits
    private static final int IV_SIZE = 16;    // IV size in bytes (128 bits)

    /**
     * Generates a random AES key.
     *
     * @return A base64 encoded string of the generated key.
     */
    public static String generateKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(KEY_SIZE, new SecureRandom()); // Initialize for AES-256
            SecretKeySpec secretKeySpec = (SecretKeySpec) keyGenerator.generateKey();
            return Base64.getEncoder().encodeToString(secretKeySpec.getEncoded());
        } catch (Exception e) {
            // Handle the exception properly in your application.  Don't just print the stack trace.
            e.printStackTrace();
            return null; // Or throw a RuntimeException, depending on your error handling policy
        }
    }

    /**
     * Generates a random initialization vector (IV).
     *
     * @return A byte array representing the IV.
     */
    public static String generateIv() {
        byte[] iv = new byte[IV_SIZE];
        new SecureRandom().nextBytes(iv);
        return Base64.getEncoder().encodeToString(iv);
    }

    /**
     * Pads a key or IV to the required length.
     *
     * @param input      The key or IV to pad.
     * @param targetLength The desired length of the padded input.
     * @return The padded byte array, or null if an error occurs.
     */
    public static byte[] pad(byte[] input, int targetLength) {
        if (input == null) {
            return null; // Handle null input
        }
        if (input.length == targetLength) {
            return input; // No padding needed
        }
        if (input.length > targetLength) {
            //  Consider logging an error or throwing an exception here.  Truncation is usually *not* what you want.
            System.err.println("Input is longer than target length.  Truncating.");
            return Arrays.copyOf(input, targetLength); // Truncate, but this is dangerous.
        }

        byte[] padded = new byte[targetLength];
        System.arraycopy(input, 0, padded, 0, input.length);
        // Pad with zeros (PKCS7 padding is more common, but for this specific case, zero padding was requested)
        for (int i = input.length; i < targetLength; i++) {
            padded[i] = 0;
        }
        return padded;
    }

    /**
     * Encrypts the given text using AES CTR with the provided key and IV.
     *
     * @param plainText The text to encrypt.
     * @param key       The encryption key (base65 encoded).
     * @param iv        The initialization vector (byte array).
     * @return A base64 encoded string of the encrypted text, or null on error.
     */
    public static byte[] encrypt(String plainText, String key, String iv) {
        try {
            if (key == null || key.isEmpty()) {
                return null;
            }
            if (iv == null || iv.isEmpty()) {
                return null;
            }

            byte[] keyBytes = Base64.getDecoder().decode(key);
            byte[] ivBytes=Base64.getDecoder().decode(iv);
            // Pad the key if necessary
                keyBytes = pad(keyBytes, KEY_SIZE / 8);
                ivBytes=pad(ivBytes,IV_SIZE);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return encryptedBytes;
        } catch (Exception e) {
            // Handle exceptions properly (logging, error messages, etc.)
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Decrypts the given text using AES CTR with the provided key and IV.
     *
     * @param encryptedText The text to decrypt (base64 encoded).
     * @param key           The decryption key (base64 encoded).
     * @param iv            The initialization vector (byte array).
     * @return The decrypted text, or null on error.
     */
    public static byte[] decrypt(byte[] encryptedText, String key, String iv) {
        try {
            if (key == null || key.isEmpty()) {
                return null;
            }
            if (iv == null || iv.isEmpty()) {
                return null;
            }
            if (encryptedText == null ) {
                return null;// Or throw an exception, depending on your needs
            }

            byte[] keyBytes = Base64.getDecoder().decode(key);
            byte[] ivBytes=Base64.getDecoder().decode(iv);
            // Pad the key if necessary
                keyBytes = pad(keyBytes, KEY_SIZE / 8);
                ivBytes=pad(ivBytes,IV_SIZE);

            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] decryptedBytes = cipher.doFinal(encryptedText);
            return decryptedBytes;
        } catch (Exception e) {
            // Handle exceptions properly.
            e.printStackTrace();
            return null;
        }
    }


}

