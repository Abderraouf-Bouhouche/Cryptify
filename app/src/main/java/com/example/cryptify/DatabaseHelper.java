package com.example.cryptify;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "namebrk.db";
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_PUBLICKEY = "publickey";
    private static final String COLUMN_PRIVATEKEY = "privatekey";

    private static final String TABLE_IMAGES = "images";
    private static final String COLUMN_IMAGE_ID = "id";
    private static final String COLUMN_IMAGE_USER = "username";
    private static final String COLUMN_IMAGE_PATH = "path";
    private static final String COLUMN_IMAGE_DATE = "date_added";
    private static final String COLUMN_IMAGE_KEY = "kkey";
    private static final String COLUMN_IMAGE_IV = "iv";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public void onCreate(SQLiteDatabase MyDatabase) {
        MyDatabase.execSQL("CREATE TABLE " + TABLE_USERS + " (" + 
            COLUMN_USERNAME + " TEXT PRIMARY KEY, " + 
            COLUMN_PASSWORD + " TEXT, " + 
            COLUMN_PUBLICKEY + " TEXT, " + 
            COLUMN_PRIVATEKEY + " TEXT)");


        MyDatabase.execSQL("CREATE TABLE " + TABLE_IMAGES + " (" +
                COLUMN_IMAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_IMAGE_USER + " TEXT, " +
                COLUMN_IMAGE_PATH + " TEXT, " +
                COLUMN_IMAGE_DATE + " TEXT, " +
                COLUMN_IMAGE_KEY + " TEXT, " +
                COLUMN_IMAGE_IV + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_IMAGE_USER + ") REFERENCES " +
                TABLE_USERS + "(" + COLUMN_USERNAME + "))");

    }

    public void onUpgrade(SQLiteDatabase MyDB, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            MyDB.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_USERS + "_new (" +
                    COLUMN_USERNAME + " TEXT PRIMARY KEY, " +
                    COLUMN_PASSWORD + " TEXT, " +
                    COLUMN_PUBLICKEY + " TEXT, " +
                    COLUMN_PRIVATEKEY + " TEXT)");

            MyDB.execSQL("INSERT INTO " + TABLE_USERS + "_new SELECT * FROM " + TABLE_USERS);
            MyDB.execSQL("DROP TABLE " + TABLE_USERS);
            MyDB.execSQL("ALTER TABLE " + TABLE_USERS + "_new RENAME TO " + TABLE_USERS);


            // Upgrade images table (new)
            MyDB.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_IMAGES + "_new (" +
                    COLUMN_IMAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_IMAGE_USER + " TEXT, " +
                    COLUMN_IMAGE_PATH + " TEXT, " +
                    COLUMN_IMAGE_DATE + " INTEGER, " +
                    COLUMN_IMAGE_KEY + " TEXT, " +
                    COLUMN_IMAGE_IV + " TEXT, " +
                    "FOREIGN KEY(" + COLUMN_IMAGE_USER + ") REFERENCES " +
                    TABLE_USERS + "(" + COLUMN_USERNAME + "))");

            MyDB.execSQL("INSERT INTO " + TABLE_IMAGES + "_new SELECT * FROM " + TABLE_IMAGES);
            MyDB.execSQL("DROP TABLE " + TABLE_IMAGES);
            MyDB.execSQL("ALTER TABLE " + TABLE_IMAGES + "_new RENAME TO " + TABLE_IMAGES);
        }
    }

    public boolean insertData(String username, String password, String publickey, String privatekey) {
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USERNAME, username);
        contentValues.put(COLUMN_PASSWORD, password);
        contentValues.put(COLUMN_PUBLICKEY, publickey);
        contentValues.put(COLUMN_PRIVATEKEY, privatekey);
        long result = MyDatabase.insert(TABLE_USERS, null, contentValues);

        return result != -1;
    }


    public boolean checkUsername(String username) {
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = MyDatabase.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = ?", new String[]{username});
            return cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            MyDatabase.close();
        }
    }

    public boolean checkUsernamePassword(String username, String password) {
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = MyDatabase.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?", new String[]{username, password});
            return cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            MyDatabase.close();
        }
    }

    public String getPublicKeyByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String publicKey = null;

        // Define the query
        String[] columns = {"publickey"};
        String selection = "username=?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(
                TABLE_USERS,  // table name
                columns,            // columns to return
                selection,          // WHERE clause
                selectionArgs,      // WHERE arguments
                null, null, null
        );

        if (cursor.moveToFirst()) {
            publicKey = cursor.getString(0);
        }

        cursor.close();
        db.close();

        return publicKey;
    }

    public String getPrivateKeyByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String privateKey= null;

        // Define the query
        String[] columns = {"privatekey"};
        String selection = "username=?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(
                TABLE_USERS,  // table name
                columns,            // columns to return
                selection,          // WHERE clause
                selectionArgs,      // WHERE arguments
                null, null, null
        );

        if (cursor.moveToFirst()) {
            privateKey= cursor.getString(0);
        }

        cursor.close();
        db.close();

        return privateKey;
    }



    public boolean insertImage(String username, String path ,
                               String key, String iv) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IMAGE_USER, username);
        values.put(COLUMN_IMAGE_PATH, path);
        values.put(COLUMN_IMAGE_DATE, System.currentTimeMillis());
        values.put(COLUMN_IMAGE_KEY, key);
        values.put(COLUMN_IMAGE_IV, iv);

        long result = db.insert(TABLE_IMAGES, null, values);
        return result != -1;
    }




    public ArrayList<ImageStructure> getImagesByUsername(String username) {
        ArrayList<ImageStructure> imageList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_IMAGES +
                        " WHERE " + COLUMN_IMAGE_USER + " = ?" +
                        " ORDER BY " + COLUMN_IMAGE_DATE + " DESC",
                new String[]{username});

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    ImageStructure image = new ImageStructure(
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_KEY)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_IV)),
                            cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_DATE))
                            // cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_TITLE))
                    );
                    imageList.add(image);
                }
            } finally {
                cursor.close();
            }
        }

        db.close();
        return imageList;
    }

}