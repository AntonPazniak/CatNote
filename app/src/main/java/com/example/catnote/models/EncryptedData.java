package com.example.catnote.models;

public class EncryptedData {

    private byte[] ciphertext;
    private byte[] initializationVector;


    public EncryptedData(byte[] ciphertext, byte[] initializationVector) {
        this.ciphertext = ciphertext;
        this.initializationVector = initializationVector;
    }

    public EncryptedData() {
    }

    public byte[] getCiphertext() {
        return ciphertext;
    }

    public void setCiphertext(byte[] ciphertext) {
        this.ciphertext = ciphertext;
    }

    public byte[] getInitializationVector() {
        return initializationVector;
    }

    public void setInitializationVector(byte[] initializationVector) {
        this.initializationVector = initializationVector;
    }
}
