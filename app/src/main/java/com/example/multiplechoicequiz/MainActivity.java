package com.example.multiplechoicequiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int requestCodeQuiz = 1;
    public static final String extraDifficulty = "extraDifficulty";
    public static final String extraCategoryID = "extraCategoryID";
    public static final String extraCategoryName = "extraCategoryName";
    //to save the high score in shared preference
    public static final String scorePreference = "ScorePreference";
    public static final String keyHighScore = "KeyHighScore";

    private TextView textViewHighScore;
    private Spinner sCategory;
    private Spinner sDifficulty;
    private int highScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //match to id
        textViewHighScore = findViewById(R.id.tvHighScore);
        sDifficulty = findViewById(R.id.sDifficulty);
        sCategory = findViewById(R.id.sCategory);
        Button btnAddNewQuestion = findViewById(R.id.btAddUserQuestion);

        loadCategories();
        loadDifficultyLevels();
        loadHighScore();//to show high score
        //to start quiz
        Button btStart = findViewById(R.id.btStartQuiz);
        btStart.setOnClickListener(this);
        //to add new question
        btnAddNewQuestion.setOnClickListener(this);

    }

    private void startQuiz() {
        //to get category user select
        Category selectedCategory = (Category) sCategory.getSelectedItem();
        int categoryID = selectedCategory.getId();
        String categoryName = selectedCategory.getName();
        //to get whatever difficulty level user select
        String difficulty = sDifficulty.getSelectedItem().toString();
        //to open QuizActivity
        Intent intent = new Intent(MainActivity.this, QuizActivity.class);
        intent.putExtra(extraCategoryID, categoryID);
        intent.putExtra(extraCategoryName, categoryName);
        intent.putExtra(extraDifficulty, difficulty);
        //to get result back
        startActivityForResult(intent, requestCodeQuiz);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //to see we have same request code or not
        if (requestCode == requestCodeQuiz) {
            if (resultCode == RESULT_OK) {//if result is ok, we want to pass to score
                int score = data.getIntExtra(QuizActivity.extraScore, 0);
                if (score > highScore){//if it's bigger to our current high score
                    updateHighScore(score);
                }
            }
        }
    }
    //put categories to spinner sCategory
    private void loadCategories() {
        QuizDataBaseHelper dbHelper = QuizDataBaseHelper.getInstance(this);
        List<Category> categories = dbHelper.getAllCategories();
        ArrayAdapter<Category> adapterCategories = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        adapterCategories.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sCategory.setAdapter(adapterCategories);
    }
    //put difficulty level to spinner sDifficluty
    private void loadDifficultyLevels() {
        //pass string array to spinner
        String[] difficultyLevels = Questions.getAllDifficultyLevels();
        //create adapter for spinner
        ArrayAdapter<String> adapterDifficulty = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, difficultyLevels);
        adapterDifficulty.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sDifficulty.setAdapter(adapterDifficulty);
    }

    private void loadHighScore() {
        SharedPreferences prefs = getSharedPreferences(scorePreference, MODE_PRIVATE);
        highScore = prefs.getInt(keyHighScore, 0);
        textViewHighScore.setText("HighScore: " + highScore);
    }

    private void updateHighScore(int newHighScore) {
        highScore = newHighScore;
        textViewHighScore.setText("HighScore: " + highScore);
        //save the value in shared preference
        SharedPreferences prefs = getSharedPreferences(scorePreference, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(keyHighScore, highScore);
        editor.apply();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btStartQuiz:
                startQuiz();
                break;
            case R.id.btAddUserQuestion:
                startActivity(new Intent(MainActivity.this, UserAddQuestion.class));
                break;
        }
    }
}
