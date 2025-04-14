package com.example.cryptify;

import java.nio.file.Path;
import java.util.Date;

public class ImageStructure {
    public String id;
    public String path;
    public String key;
    public String iv;
    public Date date_added;

    public ImageStructure(String id,String path,String key,String iv,Long date_added){
        this.id=id;
        this.path=path;
        this.key=key;
        this.iv=iv;
        this.date_added=new Date(date_added);
    }
}
