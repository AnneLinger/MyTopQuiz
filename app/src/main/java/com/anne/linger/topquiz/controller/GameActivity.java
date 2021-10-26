package com.anne.linger.topquiz.controller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.anne.linger.topquiz.R;
import com.anne.linger.topquiz.model.Question;
import com.anne.linger.topquiz.model.QuestionBank;

import java.util.Arrays;

public class GameActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView mQuestionTextView;
    private Button mAnswer1Button;
    private Button mAnswer2Button;
    private Button mAnswer3Button;
    private Button mAnswer4Button;

    private int mRemainingQuestionCount;
    private int mScore;
    private QuestionBank mQuestionBank;
    private boolean mEnableTouchEvents;

    //Clé pour la récupération du score dans le résultat de l'activité
    public static final String BUNDLE_EXTRA_SCORE = "BUNDLE_EXTRA_SCORE";

    //Clés pour la sauvegarde de l'état de l'activité en cas de onDestroy()
    public static final String BUNDLE_STATE_SCORE = "BUNDLE_STATE_SCORE";
    public static final String BUNDLE_STATE_QUESTION = "BUNDLE_STATE_QUESTION";
    public static final String BUNDLE_STATE_QUESTION_BANK = "BUNDLE_STATE_QUESTION_BANK";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Log.d("Anne","onCreate() called");

        //Clics rendus possibles
        mEnableTouchEvents=true;

        //Rattachement des éléments du xml
        mQuestionTextView = findViewById(R.id.game_activity_textview_question);
        mAnswer1Button = findViewById(R.id.game_activity_button_1);
        mAnswer2Button = findViewById(R.id.game_activity_button_2);
        mAnswer3Button = findViewById(R.id.game_activity_button_3);
        mAnswer4Button = findViewById(R.id.game_activity_button_4);

        //La classe elle-même est le listener du clic
        mAnswer1Button.setOnClickListener(this);
        mAnswer2Button.setOnClickListener(this);
        mAnswer3Button.setOnClickListener(this);
        mAnswer4Button.setOnClickListener(this);

        //Valorisation des différentes variables A SUPPRIMER ???????????
        //mCurrentQuestion=mQuestionBank.getCurrentQuestion();
        //mNextQuestion=mQuestionBank.getNextQuestion();
        //mRemainingQuestionCount=3;
        //mScore=0;

        //Vérification si une sauvegarde de l'état de l'activité est disponible => restauration du score et de la question
        if(savedInstanceState!=null){
            mScore = savedInstanceState.getInt(BUNDLE_STATE_SCORE);
            mRemainingQuestionCount = savedInstanceState.getInt(BUNDLE_STATE_QUESTION);
            mQuestionBank = (QuestionBank) savedInstanceState.getSerializable(BUNDLE_STATE_QUESTION_BANK);
        }
        else{
            mScore=0;
            mRemainingQuestionCount=3;
            mQuestionBank=generateQuestionBank();
        }

        //Affichage de la question en cours
        displayQuestion(mQuestionBank.getCurrentQuestion());

    }

    //Surcharge de méthodes pour repérer le cycle de l'activité
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

    //Méthode pour sauvegarde de l'état de l'activité en cas de onDestroy()
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(BUNDLE_STATE_SCORE, mScore);
        outState.putInt(BUNDLE_STATE_QUESTION, mRemainingQuestionCount);
        outState.putSerializable(BUNDLE_STATE_QUESTION_BANK, mQuestionBank);
    }

    //Méthode qui permet de désactiver les boutons lorsque User a fait un clic
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return mEnableTouchEvents && super.dispatchTouchEvent(ev);
    }

    //Méthode de l'interface View.OnClickListener pour détection des clics en évitant la répétition pour chaque bouton
    //Vérifie la véracité de la réponse et décide de la suite de la partie
    //Paramètre de type View = bouton sur lequel user a cliqué
    @Override
    public void onClick(View v) {
        int index;
        if(v==mAnswer1Button){
            index=0;
        }
        else if(v==mAnswer2Button){
            index=1;
        }
        else if(v==mAnswer3Button){
            index=2;
        }
        else if(v==mAnswer4Button){
            index=3;
        }
        else{
            throw new IllegalStateException("Unknown clicked view : "+v);
        }
        if(index==mQuestionBank.getCurrentQuestion().getAnswerIndex()){
            Toast.makeText(this, "Good game !!", Toast.LENGTH_SHORT).show();
            mScore++;
        }
        else{
            Toast.makeText(this, "False ! Try again !", Toast.LENGTH_SHORT).show();
        }

        mEnableTouchEvents=false;

        //Handle pour demander un délai avant l'exécution de cette partie de code
        //Vérifie si relance une question ou => la fin de la partie
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mEnableTouchEvents=true;
                mRemainingQuestionCount--;
                if(mRemainingQuestionCount>0){
                    displayQuestion(mQuestionBank.getNextQuestion());
                }
                else{
                    endGame();
                }
            }
        }, 2000);
    }

    //Méthode qui affiche la question en cours
    private void displayQuestion(final Question question){
        mQuestionTextView.setText(question.getQuestion());
        mAnswer1Button.setText(question.getChoiceList().get(0));
        mAnswer2Button.setText(question.getChoiceList().get(1));
        mAnswer3Button.setText(question.getChoiceList().get(2));
        mAnswer4Button.setText(question.getChoiceList().get(3));
    }

    //Méthode qui détermine le comportement de l'app quand la partie est finie = boite de diag + back
    private void endGame(){
        //Boite de dialogue pour le User
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Well done !!");
        builder.setMessage("Your score is "+mScore);
        builder.setCancelable(false);
        //Surveille le clic sur le OK
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            //Au clic, mémorise le résultat de l'activité et le score pour => dans main activity
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.putExtra(BUNDLE_EXTRA_SCORE, mScore);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        builder.create();
        builder.show();
    }

    //Stockage des listes de questions dans méthode de génération
    private QuestionBank generateQuestionBank(){
        Question question1 = new Question(
                "Who is the creator of Android ?",
                Arrays.asList(
                        "Andy Rubin",
                        "Steve Wozniak",
                        "Jake Wharton",
                        "Paul Smith"
                ),
                0
        );
        Question question2 = new Question(
                "When did the first man land on the moon ?",
                Arrays.asList(
                        "1958",
                        "1962",
                        "1967",
                        "1969"
                ),
                3
        );
        Question question3 = new Question(
                "What's the first name of Sheldon Cooper's best friend ?",
                Arrays.asList(
                        "Amy",
                        "Leonard",
                        "Penny",
                        "Howard"
                ),
                1
        );
        Question question4 = new Question(
                "What's the name of the House Stark's sword ?",
                Arrays.asList(
                        "Steel",
                        "Ice",
                        "Fire",
                        "Needle"
                ),
                1
        );
        Question question5 = new Question(
                "What's the initial job of Walter White ?",
                Arrays.asList(
                        "Unemployment",
                        "English teacher",
                        "Drug business",
                        "Chemistry teacher"
                ),
                3
        );
        Question question6 = new Question(
                "What country is discovered by Floki ? ",
                Arrays.asList(
                        "Iceland",
                        "France",
                        "Ireland",
                        "England"
                ),
                0
        );

        return new QuestionBank(Arrays.asList(question1, question2, question3, question4, question5, question6));
    }
}