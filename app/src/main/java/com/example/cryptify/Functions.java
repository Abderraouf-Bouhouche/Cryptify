package com.example.cryptify;

import com.example.cryptify.Steganography.*;
import com.example.cryptify.RSA.*;
public class Functions {
    RsaImplementer rsaImplementer;
    StegnoAPI stegnoAPI;

    //constructor ,initialise both the rsa functionner and the steganography functionner
    public Functions() {
        rsaImplementer = new RsaImplementer();
        stegnoAPI = new StegnoAPI();
    }





}
