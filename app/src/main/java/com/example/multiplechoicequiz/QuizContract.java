package com.example.multiplechoicequiz;

import android.provider.BaseColumns;

//this class does not do anything with app, only for creating categories and questions table.
public final class QuizContract {

    private QuizContract() {}
    //class to contain category
    public static class CategoriesTable implements BaseColumns {
        public static final String tableName = "quizCategories";
        public static final String columnName = "name";
    }

    //class to contain questions
    public static class QuestionsTable implements BaseColumns {
        //we want to access them outside of class and don't change them
        public static final String tableName = "quizQuestions";
        public static final String columnQuestion = "question";
        public static final String columnOption1 = "option1";
        public static final String columnOption2 = "option2";
        public static final String columnOption3 = "option3";
        public static final String columnAnswerNumber = "answerNumber";
        public static final String columnDifficulty = "difficulty";
        public static final String columnCategoryID = "categoryID";
    }
}