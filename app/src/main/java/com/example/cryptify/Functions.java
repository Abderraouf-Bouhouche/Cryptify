package com.example.cryptify;

import android.content.Context;

import androidx.annotation.Nullable;

import com.example.cryptify.Steganography.*;
import com.example.cryptify.RSA.*;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

public class Functions {

    //constructor ,initialise both the rsa functionner and the steganography functionner



    public static String signUp(String username, String password,@Nullable Context context){
        DatabaseHelper dbHelper=new DatabaseHelper(context);
        if(dbHelper.checkUsername(username)){
            return null;
        }
        String publicKey;
        String privateKey;
        RsaImplementer rsaImplementer=new RsaImplementer();
        password=rsaImplementer.hash(password);
        KeyPair keys=null;
        try {
        keys = rsaImplementer.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        privateKey= rsaImplementer.privateKeyToString(keys);
        publicKey= rsaImplementer.publicKeyToString(keys);

        dbHelper.insertData(username,password,publicKey,privateKey);
        return username;
    }

    public static String signIn(String username,String password,@Nullable Context context){
        DatabaseHelper dbHelper=new DatabaseHelper(context);
        RsaImplementer rsaImplementer=new RsaImplementer();
        password=rsaImplementer.hash(password);
        if(dbHelper.checkUsernamePassword(username,password)){
            return username;
        }
        return null;
    }

    public static String getPublicKey(String username,@Nullable Context context){
        DatabaseHelper dbHelper=new DatabaseHelper(context);
        String publicKey=dbHelper.getPublicKeyByUsername(username);
        return publicKey;
    }


}
