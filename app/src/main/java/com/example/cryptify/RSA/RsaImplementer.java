package com.example.cryptify.RSA;

import java.nio.charset.StandardCharsets;
import java.security.*;
import javax.crypto.Cipher;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RsaImplementer {

    public KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator =KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }


    public String encrypt(PublicKey publicKey,String message) throws Exception{
        Cipher cipher=Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE,publicKey);
        byte[] encryptedBytes=cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public String decrypt(PrivateKey privateKey,String encryptedMessage) throws Exception{
        Cipher cipher=Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.DECRYPT_MODE,privateKey);
        byte[] decryptedBytes=cipher.doFinal(Base64.getDecoder().decode(encryptedMessage));
        return  new String(decryptedBytes,StandardCharsets.UTF_8);
    }
    public String publicKeyToString(KeyPair keys){
        return Base64.getEncoder().encodeToString(keys.getPublic().getEncoded());
    }
    public String privateKeyToString(KeyPair keys){
        return Base64.getEncoder().encodeToString(keys.getPrivate().getEncoded());
    }


    public PublicKey stringToPublicKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes=Base64.getDecoder().decode(key);

           PublicKey publicKey = KeyFactory.getInstance("RSA")
                    .generatePublic(new X509EncodedKeySpec(keyBytes));
           return publicKey;
    }



    public PrivateKey stringToPrivateKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes=Base64.getDecoder().decode(key);

        PrivateKey privateKey = KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
        return privateKey;
    }

    public String hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            // Convert byte array to hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
}

