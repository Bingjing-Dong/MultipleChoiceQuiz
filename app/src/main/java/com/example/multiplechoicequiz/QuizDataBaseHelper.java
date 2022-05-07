package com.example.multiplechoicequiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.multiplechoicequiz.QuizContract.*;

import java.util.ArrayList;
import java.util.List;

public class QuizDataBaseHelper extends SQLiteOpenHelper {
    private static final String dataBaseName = "myQuiz.db";
    private static final int dataBaseVersion = 1;

    private static QuizDataBaseHelper instance;
    private SQLiteDatabase db; //hold a reference to the extra database

    public QuizDataBaseHelper(Context context) {
        super(context, dataBaseName, null, dataBaseVersion);
    }
    //to check if we already have an instance exist or not
    //instance object can only have one thread executing inside method at the same time
    public static synchronized QuizDataBaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new QuizDataBaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        //to create database table
        final String SQLCreateCategoriesTable = "Create Table " +
                CategoriesTable.tableName + "( " +
                CategoriesTable._ID + " Integer primary key Autoincrement, " +
                CategoriesTable.columnName + " Text " + ")";

        //to create question table
        final String SQLCreateQuestionsTable = "Create Table " +
                QuestionsTable.tableName + " ( " +
                QuestionsTable._ID + " Integer Primary Key Autoincrement, " +
                QuestionsTable.columnQuestion + " Text, " +
                QuestionsTable.columnOption1 + " Text, " +
                QuestionsTable.columnOption2 + " Text, " +
                QuestionsTable.columnOption3 + " Text, " +
                QuestionsTable.columnAnswerNumber + " Integer, " +
                QuestionsTable.columnDifficulty + " Text, " +
                QuestionsTable.columnCategoryID + " Integer, " +
                "Foreign Key(" + QuestionsTable.columnCategoryID + ") References " +
                CategoriesTable.tableName + "(" + CategoriesTable._ID + ")" +
                "On delete cascade" + ")";
        //to create categories and question table
        db.execSQL(SQLCreateCategoriesTable);
        db.execSQL(SQLCreateQuestionsTable);
        fillCategoriesTable();
        fillQuestionsTable();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("Drop Table if Exists " + CategoriesTable.tableName);
        db.execSQL("Drop Table if Exists " + QuestionsTable.tableName);
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    //to fill categories table
    private void fillCategoriesTable() {
        Category c1 = new Category("Programming");
        insertCategory(c1);
        Category c2 = new Category("English");
        insertCategory(c2);
        Category c3 = new Category("Math");
        insertCategory(c3);

    }

    //add one category from user
    public void addCategory(Category category) {
        db = getWritableDatabase();
        insertCategory(category);
    }

    //insert category in background
    private void insertCategory(Category category) {
        ContentValues cv = new ContentValues();
        //put name of category into table
        cv.put(CategoriesTable.columnName, category.getName());
        db.insert(CategoriesTable.tableName, null, cv);
    }

    //to fill question into table
    public void fillQuestionsTable() {
        Questions question1 = new Questions("What is the use of final keyword in Java?",
                "When a class is made final, a sublcass of it can not be created.",
                "When a method is final, it can not be overridden.", "Both is correct", 3,
                Questions.difficultEasy, Category.Programming);
        insertQuestion(question1);
        Questions question2 = new Questions("The default value of a static integer variable of a class in Java is?",
                "0", "1", "-1", 1,
                Questions.difficultMedium, Category.Programming);
        insertQuestion(question2);
        Questions question3 = new Questions("Multiple inheritance means?",
                "more classes inheriting from one super class",
                "one class inheriting from more super classes",
                "more classes inheriting from more super classes", 2,
                Questions.difficultHard, Category.Programming);
        insertQuestion(question3);
        Questions question4 = new Questions("I ___ tennis every Sunday morning",
                "playing", "play", "am playing", 2,
                Questions.difficultEasy, Category.English);
        insertQuestion(question4);
        Questions question5 = new Questions("Whose shoes are these?",
                "It's mine.",
                "They are your father's friend's shoes.", "They are hers shoes.", 2,
                Questions.difficultMedium, Category.English);
        insertQuestion(question5);
        Questions question6 = new Questions("How many students in your class ___ from China?",
                "come", "comes", "came", 1,
                Questions.difficultMedium, Category.English);
        insertQuestion(question6);
        Questions question7 = new Questions("Don't make so much noise. Jame ___ to study for his test!",
                "tries", "was trying", "is trying", 3,
                Questions.difficultHard, Category.English);
        insertQuestion(question7);
        Questions question8 = new Questions(" 3xy = 27, x = 3, what is y?",
                "3", "6", "9", 1,
                Questions.difficultEasy, Category.Math);
        insertQuestion(question8);
        Questions question9 = new Questions(" xy = 16, x = 2, what is y?",
                "2", "4", "8", 3,
                Questions.difficultMedium, Category.Math);
        insertQuestion(question9);
        Questions question10 = new Questions(" 2x = 6, what is x?",
                "1", "3", "2", 3,
                Questions.difficultHard, Category.Math);
        insertQuestion(question10);

    }

    //to add one question from user
    public void addQuestion(Questions question) {
        db = getWritableDatabase();
        insertQuestion(question);
    }

    //to insert question to question table
    private void insertQuestion(Questions question) {
        ContentValues cv = new ContentValues();
        //to get question and answer
        cv.put(QuestionsTable.columnQuestion, question.getQuestion());
        cv.put(QuestionsTable.columnOption1, question.getOption1());
        cv.put(QuestionsTable.columnOption2, question.getOption2());
        cv.put(QuestionsTable.columnOption3, question.getOption3());
        cv.put(QuestionsTable.columnAnswerNumber, question.getAnswerNumber());
        cv.put(QuestionsTable.columnDifficulty, question.getDifficulty());
        cv.put(QuestionsTable.columnCategoryID, question.getCategoryID());
        //save in database db
        db.insert(QuestionsTable.tableName, null, cv);
    }

    //to return our category list
    public List<Category> getAllCategories() {
        List<Category> categoryList = new ArrayList<>();
        db = getReadableDatabase();
        Cursor cursor = db.rawQuery( "Select * From " + CategoriesTable.tableName, null);
        if (cursor.moveToFirst()) { //recreate category java object
            do {
                Category category = new Category();
                category.setId(cursor.getInt(cursor.getColumnIndex(CategoriesTable._ID)));
                category.setName(cursor.getString(cursor.getColumnIndex(CategoriesTable.columnName)));
                categoryList.add(category);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return categoryList;
    }

    //questions list
    public ArrayList<Questions> getAllQuestions(){
        ArrayList<Questions> questionList = new ArrayList<>();
        db = getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * From " + QuestionsTable.tableName, null);

        if(cursor.moveToFirst()){
            do {
                //set question
                Questions question = new Questions();
                question.setId(cursor.getInt(cursor.getColumnIndex(QuestionsTable._ID)));
                question.setQuestion(cursor.getString(cursor.getColumnIndex(QuestionsTable.columnQuestion)));
                question.setOption1(cursor.getString(cursor.getColumnIndex(QuestionsTable.columnOption1)));
                question.setOption2(cursor.getString(cursor.getColumnIndex(QuestionsTable.columnOption2)));
                question.setOption3(cursor.getString(cursor.getColumnIndex(QuestionsTable.columnOption3)));
                question.setAnswerNumber(cursor.getInt(cursor.getColumnIndex(QuestionsTable.columnAnswerNumber)));
                question.setDifficulty(cursor.getString(cursor.getColumnIndex(QuestionsTable.columnDifficulty)));
                question.setCategoryID(cursor.getInt(cursor.getColumnIndex(QuestionsTable.columnCategoryID)));
                //add to question list
                questionList.add(question);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return questionList;
    }

    public ArrayList<Questions> getQuestions(int categoryID, String difficulty){
        ArrayList<Questions> questionList = new ArrayList<>();
        db = getReadableDatabase();
        //create a string selection to contain the query itself
        String selection = QuestionsTable.columnCategoryID + " = ? " +
                " And " + QuestionsTable.columnDifficulty + " = ? ";
        //create string to store category and difficult for our database
        String[] selectionArgs = new String[]{String.valueOf(categoryID), difficulty};
        Cursor cursor = db.query(QuestionsTable.tableName, null, selection, selectionArgs,
                null, null, null);

        if(cursor.moveToFirst()){
            do {//set question
                Questions question = new Questions();
                question.setId(cursor.getInt(cursor.getColumnIndex(QuestionsTable._ID)));
                question.setQuestion(cursor.getString(cursor.getColumnIndex(QuestionsTable.columnQuestion)));
                question.setOption1(cursor.getString(cursor.getColumnIndex(QuestionsTable.columnOption1)));
                question.setOption2(cursor.getString(cursor.getColumnIndex(QuestionsTable.columnOption2)));
                question.setOption3(cursor.getString(cursor.getColumnIndex(QuestionsTable.columnOption3)));
                question.setAnswerNumber(cursor.getInt(cursor.getColumnIndex(QuestionsTable.columnAnswerNumber)));
                question.setDifficulty(cursor.getString(cursor.getColumnIndex(QuestionsTable.columnDifficulty)));
                question.setCategoryID(cursor.getInt(cursor.getColumnIndex(QuestionsTable.columnCategoryID)));
                questionList.add(question);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return questionList;
    }
}
