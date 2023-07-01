package com.example.catnote.models;

public class User {

    private String login;
    private String salt;
    private String text;
    private String textFP;
    private String iv;


    public User(String login, String salt, String text,String textFP,String iv) {
        this.login = login;
        this.salt = salt;
        this.text = text;
        this.textFP = textFP;
        this.iv = iv;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTextFP() {
        return textFP;
    }

    public void setTextFP(String textFP) {
        this.textFP = textFP;
    }
}
