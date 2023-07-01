package com.example.catnote.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.catnote.MainActivity;
import com.example.catnote.models.User;
import com.example.catnote.test.Login;
import com.example.catnote.until.Until;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class DBCatNote extends SQLiteOpenHelper {


    public DBCatNote(Context context) {
        super(context, Until.DATABASE_NAME, null, Until.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_USER_TABLE = "CREATE TABLE "+Until.TABlE_NAME+ " ("
                +Until.KEY_LOGIN+" TEXT , "
                +Until.KEY_SALT+" TEXT, "
                +Until.KEY_TEXT+" TEXT, "
                +Until.KEY_TEXT_FP+" TEXT, "
                +Until.KEY_IV +" TEXT "+ " )";
        System.out.println(CREATE_USER_TABLE);
        sqLiteDatabase.execSQL(CREATE_USER_TABLE);

    }

    public void clear(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS "+Until.TABlE_NAME);
        onCreate(db);
    }

    public void test() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, UnrecoverableKeyException, CertificateException, KeyStoreException, IOException {
        String salt = Login.randomSalt();
        String text = Login.encryption(salt+"kurwa",Login.getHash(salt,"1111"));
        User user = new User("popka",salt,text, null,null);
        setUser(user);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+Until.TABlE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void setUser(User user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(Until.KEY_LOGIN, user.getLogin());
        contentValues.put(Until.KEY_SALT,user.getSalt());
        contentValues.put(Until.KEY_TEXT, user.getText());
        contentValues.put(Until.KEY_TEXT_FP, user.getTextFP());
        contentValues.put(Until.KEY_IV, user.getTextFP());

        db.insert(Until.TABlE_NAME, null , contentValues);
        db.close();

    }

    public User getUser(String login){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Until.TABlE_NAME,new String[]{Until.KEY_LOGIN,Until.KEY_SALT,Until.KEY_TEXT,Until.KEY_TEXT_FP,Until.KEY_IV},
                Until.KEY_LOGIN+ "=?" ,new String[]{String.valueOf(login)},null,null,null,null);
        if ( cursor != null)
            cursor.moveToFirst();
        db.close();
        if(cursor != null)
            return new User(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4));
        else return null;
    }

    public Boolean updateUser(String pasOld, String pasNew) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {

        User user = getUser("popka");


        String salt = user.getSalt();
        String hash = Login.getHash(salt,pasOld);


        try {

            String text = Login.decryption(user.getText(), hash);

            if (salt.equals(text.substring(0, 24))) {
                text = text.substring(24);
                salt = Login.randomSalt();
                text = salt+text;
                hash = Login.getHash(salt,pasNew);
                String text0 = Login.encryption(text, hash);

                SQLiteDatabase db = this.getWritableDatabase();
                ContentValues contentValues = new ContentValues();

                contentValues.put(Until.KEY_SALT, salt);
                contentValues.put(Until.KEY_TEXT, text0);

                db.update(Until.TABlE_NAME, contentValues, Until.KEY_LOGIN + "=?", new String[]{String.valueOf("popka")});
                return true;
            } else return false;
        }catch (Exception ignored){}
        return false;
    }


    public Boolean updateUserText(String pasOld, String text) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {

        User user = getUser("popka");

        String salt = user.getSalt();
        String hash = Login.getHash(salt,pasOld);

        try {

            String text0 = Login.decryption(user.getText(), hash);

            if (salt.equals(text0.substring(0, 24))) {

                SQLiteDatabase db = this.getWritableDatabase();
                ContentValues contentValues = new ContentValues();

                text = Login.encryption(salt+text, hash);
                contentValues.put(Until.KEY_TEXT, text);
                System.out.println("я тут");

                db.update(Until.TABlE_NAME, contentValues, Until.KEY_LOGIN + "=?", new String[]{String.valueOf("popka")});
                return true;
            }
        }catch (Exception ignored){}
        return false;
    }

    public void updateUserFinger(String text, String iv) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Until.KEY_TEXT_FP, text);
        contentValues.put(Until.KEY_IV, iv);
        db.update(Until.TABlE_NAME, contentValues, Until.KEY_LOGIN + "=?", new String[]{String.valueOf("popka")});

    }

}

