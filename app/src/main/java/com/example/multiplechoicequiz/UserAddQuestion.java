package com.example.multiplechoicequiz;


import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class UserAddQuestion extends AppCompatActivity {
    private Button btnSubmit;
    private Button btnCancel;
    private EditText etQuestion;
    private EditText etOption1;
    private EditText etOption2;
    private EditText etOption3;
    private EditText etAnswerNr;
    private Spinner sSelectCategory;
    private Spinner sSelectDifficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addquestion);
        etQuestion = findViewById(R.id.etQuestion);
        etOption1 = findViewById(R.id.etOption1);
        etOption2 = findViewById(R.id.etOption2);
        etOption3 = findViewById(R.id.etOption3);
        etAnswerNr = findViewById(R.id.etAnswerNumber);
        btnSubmit = findViewById(R.id.btSubmit);
        btnCancel = findViewById(R.id.btCancel);
        sSelectCategory = findViewById(R.id.sSelectCategory);
        sSelectDifficulty = findViewById(R.id.sSelectDifficulty);

        loadCategories();
        loadDifficultyLevels();

        final Context context = UserAddQuestion.this;

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if any details is missing and left empty
                if (etQuestion.getText().toString().isEmpty() || etOption1.getText().toString().isEmpty() ||
                        etOption2.getText().toString().isEmpty() || etOption3.getText().toString().isEmpty()
                        || etAnswerNr.getText().toString().isEmpty()) {
                    Toast.makeText(UserAddQuestion.this,"Please Fill All the Details", Toast.LENGTH_SHORT).show();
                }
                else {//add question to database we have
                    String difficulty = sSelectDifficulty.getSelectedItem().toString();
                    Category selectedCategory = (Category) sSelectCategory.getSelectedItem();
                    int categoryID = selectedCategory.getId();
                    Questions question = new Questions(etQuestion.getText().toString(), etOption1.getText().toString(),
                            etOption2.getText().toString(), etOption3.getText().toString(),
                            Integer.parseInt(etAnswerNr.getText().toString()), difficulty , categoryID);
                    QuizDataBaseHelper dbHelper = new QuizDataBaseHelper(context);
                    dbHelper.addQuestion(question); //add question to database
                    //after add, show message 'Successful' to user
                    Toast.makeText(UserAddQuestion.this,"Successful!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                finish();
            }
        });
    }

    // load categories for spinner sSelectCategory
    private void loadCategories() {
        QuizDataBaseHelper dbHelper = QuizDataBaseHelper.getInstance(this);
        List<Category> categories = dbHelper.getAllCategories();
        ArrayAdapter<Category> adapterCategories = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        adapterCategories.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sSelectCategory.setAdapter(adapterCategories);
    }

    //load difficulty levels for spinner sSelectDifficulty
    private void loadDifficultyLevels() {
        //pass string array to spinner
        String[] difficultyLevels = Questions.getAllDifficultyLevels();
        //create adapter for spinner
        ArrayAdapter<String> adapterDifficulty = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, difficultyLevels);
        adapterDifficulty.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sSelectDifficulty.setAdapter(adapterDifficulty);
    }

}
