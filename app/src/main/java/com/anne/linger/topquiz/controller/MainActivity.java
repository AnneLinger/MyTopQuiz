package com.anne.linger.topquiz.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.anne.linger.topquiz.R;
import com.anne.linger.topquiz.model.User;

public class MainActivity extends AppCompatActivity {

    private TextView mGreetingTextView;
    private EditText mNameEditText;
    private Button mPlayButton;

    //Clé pour l'identification de l'activité où données à récupérer (contexte)
    private static final int GAME_ACTIVITY_REQUEST_CODE = 2;

    //Clé pour stockage des données dans les préférences (contexte, prénom et score)
    private static final String SHARED_PREF_USER_INFO = "SHARED_PREF_USER_INFO";
    private static final String SHARED_PREF_USER_INFO_NAME = "SHARED_PREF_USER_INFO_NAME";
    private static final String SHARED_PREF_USER_INFO_SCORE = "SHARED_PREF_USER_INFO_SCORE";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("Anne","onCreate() called");

        mGreetingTextView = findViewById(R.id.main_textview_greeting);
        mNameEditText = findViewById(R.id.main_edittext_name);
        mPlayButton = findViewById(R.id.main_button_play);

        User mUser = new User();

        mPlayButton.setEnabled(false);

        //Listener sur zone de frappe pour mettre le bouton en actif
        mNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mPlayButton.setEnabled(!s.toString().isEmpty());
            }

        //Variable pour récupérer le prénom stocké dans les SharedPreferences
        String firstName = getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE).getString(SHARED_PREF_USER_INFO_NAME, null);

        });

        //Démarrage de seconde activité lors du clic
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE)
                        .edit()
                        .putString(SHARED_PREF_USER_INFO_NAME, mUser.getFirstName())
                        .apply();

                Intent gameActivityIntent = new Intent(MainActivity.this, GameActivity.class);
                startActivityForResult(gameActivityIntent, GAME_ACTIVITY_REQUEST_CODE);

                mUser.setFirstName(mNameEditText.getText().toString());
            }
        });

        //Vérifie si User a déjà joué
        greetUser();
    }

    //Surcharges de méthodes pour repérer le cycle de l'activité
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Anne","onStart() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Anne","onResume() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Anne","onPause() called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Anne","onStop() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Anne","onDestroy() called");
    }

    //Surcharge de méthode qui récupère le résultat de la 2de activité
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Vérifie que résultat de la bonne activité et que l'activité s'est bien terminée
        if (GAME_ACTIVITY_REQUEST_CODE == requestCode && RESULT_OK == resultCode) {
            //Récupère le score via sa clé définie dans la méthode de fin de game activity
            int score = data.getIntExtra(GameActivity.BUNDLE_EXTRA_SCORE, 0);

            //Stocke le score dans SharedPreferences
            getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE)
                    .edit()
                    .putInt(SHARED_PREF_USER_INFO_SCORE, score)
                    .apply();
        }
        greetUser();
    }

    //Méthode qui module l'affichage de 1re activité selon que User a déjà joué ou non
    private void greetUser(){
        String firstName = getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE).getString(SHARED_PREF_USER_INFO_NAME, null);
        int score = getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE).getInt(SHARED_PREF_USER_INFO_SCORE, -1);

        if(firstName!=null){
            if(score!=-1){
                mGreetingTextView.setText("Welcome back "+firstName+" ! Your last score was "+score+" , will you do better this time ?");
            }
            else{
                mGreetingTextView.setText("Welcome back "+firstName+" !");
            }
            mNameEditText.setText(firstName);
        }

    }
}