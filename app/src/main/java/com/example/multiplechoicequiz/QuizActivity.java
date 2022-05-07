package com.example.multiplechoicequiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class QuizActivity extends AppCompatActivity {
    //also use extraScore in MainActivity
    public static final String extraScore = "extraScore";
    private static final long countdownInMillis = 30000; //30 second
    //to save value when user rotate the screen
    private static final String keyScore = "keyScore";
    private static final String keyQuestionCount = "keyQuestionCount";
    private static final String keyMillisLeft = "keyMillisLeft";
    private static final String keyAnswered = "keyAnswered";
    private static final String keyQuestionList = "keyQuestionList";
    //variable to match id in .xml file
    private TextView tvQuestion;
    private TextView tvScore;
    private TextView tvQuestionCount;
    private TextView tvCategory;
    private TextView tvDifficulty;
    private TextView tvTime;
    private RadioGroup btRadioGroup;
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;
    private Button btConfirmNext;
    //default color
    private ColorStateList textColorDefaultRb;
    private ColorStateList textColorDefaultTime;
    //count down timer
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;

    private ArrayList<Questions> questionsList;
    private int questionCounter;
    private int questionCountTotal;
    private Questions currentQuestion;
    private int score;
    //to check answer
    private boolean answered;
    //save action when user press back
    private long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        //match to .xml id
        tvQuestion = findViewById(R.id.tvQuestion);
        tvScore = findViewById(R.id.tvScore);
        tvQuestionCount = findViewById(R.id.tvQuestionCount);
        tvCategory = findViewById(R.id.tvCategory);
        tvDifficulty = findViewById(R.id.tvDifficulty);
        tvTime = findViewById(R.id.tvTime);
        btRadioGroup = findViewById(R.id.btRadioGroup);
        rb1 = findViewById(R.id.rbOption1);
        rb2 = findViewById(R.id.rbOption2);
        rb3 = findViewById(R.id.rbOption3);
        btConfirmNext = findViewById(R.id.btConfirmNext);
        //set default text color for radio button and countdown timer
        textColorDefaultRb = rb1.getTextColors();
        textColorDefaultTime = tvTime.getTextColors();

        Intent intent = getIntent();
        int categoryID = intent.getIntExtra(MainActivity.extraCategoryID, 0);
        String categoryName = intent.getStringExtra(MainActivity.extraCategoryName);
        String difficulty = intent.getStringExtra(MainActivity.extraDifficulty);
        tvCategory.setText("Category: " + categoryName);
        tvDifficulty.setText("Difficulty: " + difficulty);

        if (savedInstanceState == null) {
            //initialize the QuizDataBaseHelper
            QuizDataBaseHelper dbHelper = QuizDataBaseHelper.getInstance(this);
            questionsList = dbHelper.getQuestions(categoryID, difficulty); //to create our database
            questionCountTotal = questionsList.size();
            //to get our question in random order
            Collections.shuffle(questionsList);
            //showing next question
            showNextQuestion();
        }
        else {//to restore the previous state
            questionsList = savedInstanceState.getParcelableArrayList(keyQuestionList);
            questionCountTotal = questionsList.size();
            questionCounter = savedInstanceState.getInt(keyQuestionCount);
            currentQuestion = questionsList.get(questionCounter - 1);
            score = savedInstanceState.getInt(keyScore);
            timeLeftInMillis = savedInstanceState.getLong(keyMillisLeft);
            answered = savedInstanceState.getBoolean(keyAnswered);
            //if the question has no answer yet
            if (!answered) {
                startCountDown();
            }
            else {//if answered, check answer
                updateCountDownText();
                showSolution();
            }
        }

        btConfirmNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!answered) { //if user selected any answer
                    if (rb1.isChecked() || rb2.isChecked() || rb3.isChecked()) {
                        checkAnswer();
                    }
                    else { //if no radio button is select and try to click the confirm button
                        Toast.makeText(QuizActivity.this,"Please select an answer", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    showNextQuestion();
                }
            }
        });
    }

    private void showNextQuestion(){
        //make 3 option to default color
        rb1.setTextColor(textColorDefaultRb);
        rb2.setTextColor(textColorDefaultRb);
        rb3.setTextColor(textColorDefaultRb);
        btRadioGroup.clearCheck();
        //to check if it's last question
        if (questionCounter < questionCountTotal) {
            currentQuestion = questionsList.get(questionCounter);
            //show question and option answer
            tvQuestion.setText(currentQuestion.getQuestion());
            rb1.setText(currentQuestion.getOption1());
            rb2.setText(currentQuestion.getOption2());
            rb3.setText(currentQuestion.getOption3());
            //to show which question you are and total question number
            questionCounter++;
            tvQuestionCount.setText("Question: " + questionCounter + "/" + questionCountTotal);
            answered = false;
            btConfirmNext.setText("Confirm");
            //begin to countdown time
            timeLeftInMillis = countdownInMillis;
            startCountDown();
        }
        else{
            finishQuiz();
        }
    }

    private void startCountDown() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {//every 1 sceond
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 0;
                updateCountDownText();
                checkAnswer(); //if user choose an answer, then check correct or not
            }
        }.start();
    }

    private void updateCountDownText() {
        //get minutes and seconds
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        //put in format
        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        tvTime.setText(timeFormatted);
        //if time is less than 10s
        if (timeLeftInMillis < 10000) {
            tvTime.setTextColor(Color.RED); //change color to red
        }
        else {
            tvTime.setTextColor(textColorDefaultTime);
        }
    }

    private void checkAnswer() {
        answered = true;

        countDownTimer.cancel();
        //return the radio button id that user select
        RadioButton rbSelected = findViewById(btRadioGroup.getCheckedRadioButtonId());
        int answerNumber = btRadioGroup.indexOfChild(rbSelected) + 1;
        //if select answer number is the true answer
        if(answerNumber == currentQuestion.getAnswerNumber()){
            score++;
            tvScore.setText("Score: " + score);
        }
        showSolution();
    }

    private void showSolution() {
        //set all color to red
        rb1.setTextColor(Color.RED);
        rb2.setTextColor(Color.RED);
        rb3.setTextColor(Color.RED);
        //switch to green color for correct answer
        switch (currentQuestion.getAnswerNumber()) {
            case 1:
                rb1.setTextColor(Color.GREEN);
                tvQuestion.setText("Answer 1 is correct");
                break;
            case 2:
                rb2.setTextColor(Color.GREEN);
                tvQuestion.setText("Answer 2 is correct");
                break;
            case 3:
                rb3.setTextColor(Color.GREEN);
                tvQuestion.setText("Answer 3 is correct");
                break;
        }
        //to check if it's the last question or any question left
        if(questionCounter < questionCountTotal) {
            btConfirmNext.setText("Next");
        }
        else {
            btConfirmNext.setText("Finish");
        }
    }

    private void finishQuiz() {
        //we want to back our starting screen
        Intent resultIntent = new Intent();
        resultIntent.putExtra(extraScore, score);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        //if user press back twice in 2 second
        if(backPressedTime + 2000 > System.currentTimeMillis()) {
            finishQuiz();//finish quiz
        }
        else {//show user that need to press back again
            Toast.makeText(this, "Press back again to finish quiz", Toast.LENGTH_SHORT).show();
        }
        //save current time to backPressedTime, will be compare to next time press back
        backPressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {//restart countdown timer every question
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {//save user value
        super.onSaveInstanceState(outState);
        outState.putInt(keyScore, score);
        outState.putInt(keyQuestionCount, questionCounter);
        outState.putLong(keyMillisLeft, timeLeftInMillis);
        outState.putBoolean(keyAnswered, answered);
        outState.putParcelableArrayList(keyQuestionList, questionsList);
    }
}
