package com.example.catnote;

import static javax.crypto.Cipher.DECRYPT_MODE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.catnote.data.DBCatNote;
import com.example.catnote.models.CryptographyManager;
import com.example.catnote.models.EncryptedData;
import com.example.catnote.models.User;
import com.example.catnote.test.Login;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Executor;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextLogin;
    private EditText editTextPassword;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private User user;
    private CryptographyManager cryptographyManager = new CryptographyManager();
    private byte[] initializationVector;
    private Boolean readyToEncrypt = false;
    private byte[] ciphertext;
    private SecretKey secretKey0;

    private Boolean test = false;


    IvParameterSpec ivParameterSpec;

    DBCatNote dbCatNote = new DBCatNote(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        this.user = dbCatNote.getUser("popka");
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometrxicPrompt(LoginActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                                "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                try {
                    processData(result.getCryptoObject());
                } catch (IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(),
                        "Authentication succeeded!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                                Toast.LENGTH_SHORT)
                        .show();
            }
        });
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for my app")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use account password")
                .build();
        ImageView biometricLoginButton = findViewById(R.id.imageView);
        biometricLoginButton.setOnClickListener(view -> {
            biometricPrompt.authenticate(promptInfo);
        });
        if (user.getIv()!=null) {
            try {
                authenticateToDecrypt();
            } catch (InvalidAlgorithmParameterException | UnrecoverableKeyException | NoSuchPaddingException | CertificateException | NoSuchAlgorithmException | KeyStoreException | IOException | NoSuchProviderException | InvalidKeyException e) {
                e.printStackTrace();
            }
        }

    }


    private void processData(BiometricPrompt.CryptoObject cryptoObject) throws IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
        this.user = dbCatNote.getUser("popka");
        String data = null;
        if (readyToEncrypt) {
            String text = user.getSalt()+"popka jsjd gj g; sdg sdg ;sd g;sdg  dsg";
            EncryptedData encryptedData = cryptographyManager.encryptData(text, Objects.requireNonNull(cryptoObject.getCipher()));
            ciphertext = encryptedData.getCiphertext();
            initializationVector = encryptedData.getInitializationVector();

            data = Login.encode(ciphertext);
            dbCatNote.updateUserFinger(data,Login.encode(initializationVector));
            String s = Login.encode(initializationVector);
            test = true;
        }
        else {
                mainScreen(cryptographyManager.decryptData(Login.decode(user.getTextFP()), Objects.requireNonNull(cryptoObject.getCipher())));
        }
    }

    public void authenticateToEncrypt() throws InvalidAlgorithmParameterException, UnrecoverableKeyException, NoSuchPaddingException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, NoSuchProviderException, InvalidKeyException {
        readyToEncrypt = true;
        if (BiometricManager.from(getApplicationContext()).canAuthenticate() == BiometricManager
                .BIOMETRIC_SUCCESS) {
            Cipher cipher = cryptographyManager.getInitializedCipherForEncryption("secretKeyName");
            biometricPrompt.authenticate(promptInfo, new BiometricPrompt.CryptoObject(cipher));
        }
    }

    private void authenticateToDecrypt() throws InvalidAlgorithmParameterException, UnrecoverableKeyException, NoSuchPaddingException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, NoSuchProviderException, InvalidKeyException {
        readyToEncrypt = false;
        if (BiometricManager.from(getApplicationContext()).canAuthenticate() == BiometricManager
                .BIOMETRIC_SUCCESS) {
            Cipher cipher = cryptographyManager.getInitializedCipherForDecryption("secretKeyName",Login.decode(user.getIv()));
            biometricPrompt.authenticate(promptInfo, new BiometricPrompt.CryptoObject(cipher));
        }
    }

    private void mainScreen(String string) {
        MainActivity.setText(string.substring(24));
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onClickButtonSignUp(View v) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException, UnrecoverableKeyException, CertificateException, KeyStoreException, IOException, InvalidAlgorithmParameterException, NoSuchProviderException {
        dbCatNote.clear();
        dbCatNote.test();
    }

    public void onClickImage(View v) {
        Intent intent = new Intent(this, TestActivity.class);
        startActivity(intent);
    }

    public void onClickButtonLogin(View v) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, UnrecoverableKeyException, CertificateException, IOException, KeyStoreException, NoSuchProviderException {

        editTextPassword = (EditText) findViewById(R.id.pwdPassword);
        if(!editTextPassword.getText().toString().equals("")) {
            try {
                if (user != null) {
                    String salt = user.getSalt();
                    String hash = Login.getHash(salt,editTextPassword.getText().toString());
                    String text = Login.decryption(user.getText(), hash);
                    if (salt.equals(text.substring(0, 24))) {
                        mainScreen(text);
                    }
                }
            }catch (Exception ignored){
                Toast.makeText(getApplicationContext(),
                        "Wrong password!", Toast.LENGTH_SHORT).show();
            }
        }
    }

}