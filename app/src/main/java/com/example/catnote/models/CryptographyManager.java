package com.example.catnote.models;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class CryptographyManager {

    private final int KEY_SIZE = 256;
    private final String ANDROID_KEYSTORE = "AndroidKeyStore";
    private final String ENCRYPTION_PADDING = KeyProperties.ENCRYPTION_PADDING_NONE;
    private final String ENCRYPTION_ALGORITHM = KeyProperties.KEY_ALGORITHM_AES;



    public Cipher gettCipher (String keyName) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException, UnrecoverableKeyException, CertificateException, KeyStoreException, IOException, NoSuchProviderException {
        Cipher cipher = getCipher();
        SecretKey secretKey = getOrCreateSecretKey(keyName);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher;
    }

    public Cipher getInitializedCipherForEncryption(String keyName) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, UnrecoverableKeyException, CertificateException, KeyStoreException, IOException, NoSuchProviderException, InvalidKeyException {
        Cipher cipher = getCipher();
        SecretKey secretKey = getOrCreateSecretKey(keyName);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher;
    }

    public Cipher getInitializedCipherForDecryption(String keyName, byte[] initializationVector) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, UnrecoverableKeyException, CertificateException, KeyStoreException, IOException, NoSuchProviderException, InvalidKeyException {
        Cipher cipher = getCipher();
        SecretKey secretKey = getOrCreateSecretKey(keyName);
        cipher.init(Cipher.DECRYPT_MODE, secretKey,new IvParameterSpec(initializationVector));
        return cipher;
    }

    public EncryptedData encryptData(String plaintext, Cipher cipher ) throws IllegalBlockSizeException, BadPaddingException {
        byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        return new EncryptedData(ciphertext,cipher.getIV());
    }

    public String decryptData(byte [] ciphertext, Cipher cipher) throws IllegalBlockSizeException, BadPaddingException {
        byte[] plaintext = cipher.doFinal(ciphertext);
        return new String(plaintext);
    }

    private Cipher getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
        return Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                + KeyProperties.BLOCK_MODE_CBC + "/"
                + KeyProperties.ENCRYPTION_PADDING_PKCS7);
    }

    public SecretKey getOrCreateSecretKey(String keyName) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException, NoSuchProviderException, InvalidAlgorithmParameterException {
        // If Secretkey was previously created for that keyName, then grab and return it.
        KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
        keyStore.load(null);
        SecretKey secretKey = ((SecretKey)keyStore.getKey("KEY_NAME", null));
        if(secretKey != null){
            return secretKey;
        }

        KeyGenParameterSpec generateSecretKey = (new KeyGenParameterSpec.Builder(
                keyName,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setUserAuthenticationRequired(true)
                .setKeySize(KEY_SIZE)
                .build());

        KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,
                ANDROID_KEYSTORE);
        keyGenerator.init(generateSecretKey);
        return keyGenerator.generateKey();
    }


}
