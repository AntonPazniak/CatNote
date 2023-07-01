package com.example.catnote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.catnote.data.DBCatNote;
import com.example.catnote.models.CryptographyManager;
import com.example.catnote.models.EncryptedData;
import com.example.catnote.models.User;
import com.example.catnote.test.Login;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class SettingsActivity extends AppCompatActivity {

    EditText editTextPassword0;
    EditText editTextPassword1;
    EditText editTextPassword2;
    DBCatNote dbCatNote = new DBCatNote(this);
    private static Cipher cipher;

    public static void setCipher(Cipher cipher) {
        SettingsActivity.cipher = cipher;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        User user = dbCatNote.getUser("popka");
        Switch mySwitch = findViewById(R.id.switch0);
        mySwitch.setChecked(user.getIv()!=null);
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                User user = dbCatNote.getUser("popka");
                editTextPassword0 = (EditText) findViewById(R.id.pwdPassword);
                try {
                    if (user.getIv() == null && !editTextPassword0.getText().toString().equals("")&&b) {
                        String salt = user.getSalt();
                        String hash = Login.getHash(salt, editTextPassword0.getText().toString());
                        String text = Login.decryption(user.getText(), hash);
                        if (salt.equals(text.substring(0, 24))) {
                            CryptographyManager cryptographyManager = new CryptographyManager();
                            EncryptedData encryptedData = cryptographyManager.encryptData(text, cryptographyManager.gettCipher("secretKeyName"));
                            dbCatNote.updateUserFinger(Login.encode(encryptedData.getCiphertext()), Login.encode(encryptedData.getInitializationVector()));
                            Toast.makeText(getApplicationContext(),
                                    "Fingerprint authentication enabled", Toast.LENGTH_SHORT).show();
                        } else {
                            mySwitch.setChecked(false);
                            Toast.makeText(getApplicationContext(),
                                    "Wrong password!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        mySwitch.setChecked(false);
                        dbCatNote.updateUserFinger(null, null);
                        Toast.makeText(getApplicationContext(),
                                "Fingerprint authentication disabled!", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    mySwitch.setChecked(false);
                    Toast.makeText(getApplicationContext(),
                            "Wrong password!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void onClickButtonChange(View v) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        editTextPassword0 = (EditText) findViewById(R.id.pwdPassword);
        editTextPassword1 = (EditText) findViewById(R.id.pwdPasswordNew);
        editTextPassword2 = (EditText) findViewById(R.id.pwdPasswordNew0);
        if(
                !editTextPassword0.getText().toString().equals("")&&
                !editTextPassword1.getText().toString().equals("")&&
                !editTextPassword2.getText().toString().equals("")&&
                editTextPassword1.getText().toString().equals(editTextPassword2.getText().toString())&&
                !editTextPassword0.getText().toString().equals(editTextPassword1.getText().toString())
        ){
            if(dbCatNote.updateUser(editTextPassword0.getText().toString(),editTextPassword1.getText().toString()))
                Toast.makeText(getApplicationContext(),
                        "Password changed", Toast.LENGTH_SHORT).show();
        }else
            Toast.makeText(getApplicationContext(),
                    "Wrong password!", Toast.LENGTH_SHORT).show();
    }

    public void onClickButtonBack(View v){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}



