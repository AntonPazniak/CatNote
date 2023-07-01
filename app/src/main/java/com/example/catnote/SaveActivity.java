package com.example.catnote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.catnote.data.DBCatNote;
import com.example.catnote.models.CryptographyManager;
import com.example.catnote.models.EncryptedData;
import com.example.catnote.models.User;
import com.example.catnote.test.Login;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class SaveActivity extends AppCompatActivity {

    static String text;
    EditText editTextPassword;
    DBCatNote dbCatNote = new DBCatNote(this);


    public static void setText(String text) {
        SaveActivity.text = text;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);
    }

    public void onClickButtonBack(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onClickButtonSave(View v) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
        editTextPassword = (EditText) findViewById(R.id.pwdPasswordSave);

        try {

            if (!editTextPassword.getText().toString().equals("")) {
                if (dbCatNote.updateUserText(editTextPassword.getText().toString(), text)) {
                    MainActivity.setText(text);
                    User user = dbCatNote.getUser("popka");
                    if(user.getIv()!=null){
                        CryptographyManager cryptographyManager = new CryptographyManager();
                        EncryptedData encryptedData = cryptographyManager.encryptData(user.getSalt()+text,cryptographyManager.gettCipher("secretKeyName"));
                        dbCatNote.updateUserFinger(Login.encode(encryptedData.getCiphertext()),Login.encode(encryptedData.getInitializationVector()));
                    }
                    Toast.makeText(getApplicationContext(),
                            "Saved!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),
                            "Wrong password!", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(getApplicationContext(),
                        "Wrong password!", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception ignored){
            Toast.makeText(getApplicationContext(),
                    "Wrong password!", Toast.LENGTH_SHORT).show();
        }

    }
}
