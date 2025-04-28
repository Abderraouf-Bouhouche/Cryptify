package com.example.cryptify;

import java.nio.file.Path;
import java.util.Date;

public class ImageStructure {
    public int id;
    public String image;
    public String key;
    public String iv;
    public Date date_added;

    public ImageStructure(int id,String image,String key,String iv,Long date_added){
        this.id=id;
        this.image=image;
        this.key=key;
        this.iv=iv;
        this.date_added=new Date(date_added);
    }
}
