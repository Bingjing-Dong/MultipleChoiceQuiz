package com.example.multiplechoicequiz;

public class Category {
    public static final int Programming = 1; //category id number
    public static final int English = 2;
    public static final int Math = 3;

    private int id;
    private String name;

    public Category() {}
    //constructor
    public Category(String name) {
        this.name = name;
    }
    //setter and getter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
