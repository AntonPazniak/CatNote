package com.example.catnote.test;

import android.content.SharedPreferences;
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
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Login {

    private static String scp = "kdsjgksdg";


    public static String randomSalt(){
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return encode(salt);
    }

    public static String getHash(String salt,String pas) throws NoSuchAlgorithmException, InvalidKeySpecException {

        KeySpec spec = new PBEKeySpec(pas.toCharArray(), salt.getBytes(), 65536, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = factory.generateSecret(spec).getEncoded();
        return encode(hash);
    }

    public static Boolean checkHash(String salt,String pas, String hash) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return  hash.equals(getHash(salt,pas));
    }

    public static String encryption(String s,String key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE,generateKey(key));
        return encode(cipher.doFinal(s.getBytes()));
    }

    public static String decryption(String s,String key) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        byte[] bytes = decode(s);
        Cipher cipher1 = Cipher.getInstance("AES");
        cipher1.init(Cipher.DECRYPT_MODE,generateKey(key));
        return  new String(cipher1.doFinal(bytes));
    }

    public static SecretKeySpec generateKey(String s) {
        final byte[] finalKey = new byte[32];
        int i = 0;
        for(byte b : s.getBytes(StandardCharsets.UTF_8))
            finalKey[i++%32] ^= b;
        return new SecretKeySpec(finalKey, "AES");
    }

    public static String encode(byte[] b){
        return Base64.getEncoder().encodeToString(b);
    }
    public static byte[] decode(String s){
        return Base64.getDecoder().decode(s);
    }


}
