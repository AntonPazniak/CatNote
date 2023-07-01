package com.example.catnote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.catnote.data.DBCatNote;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static String text;
    private TextInputEditText textInputEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DBCatNote dbCatNote = new DBCatNote(this);
        textInputEditText = (TextInputEditText) findViewById(R.id.textView2);
        textInputEditText.setText(text);
        System.out.println(text);
    }


    public void onClickButtonLogout(View v){
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void onClickButtonSettings(View v){
        Intent intent = new Intent(this,SettingsActivity.class);
        startActivity(intent);
    }

    public void onClickButtonSave(View v){
        textInputEditText = (TextInputEditText) findViewById(R.id.textView2);
        SaveActivity.setText(Objects.requireNonNull(textInputEditText.getText()).toString());
        Intent intent = new Intent(this,SaveActivity.class);
        startActivity(intent);
        finish();
    }

    public static void setText(String text) {
        MainActivity.text = text;
    }



}