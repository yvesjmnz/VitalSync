package com.example.vitalsync;

public class Meal {
    private String name;
    private String time;
    private float calories;
    private float proteins;
    private float fats;
    private float carbs;


    public Meal(String name, String time, float calories, float proteins, float fats, float carbs) {
        this.name = name;
        this.time = time;
        this.calories = calories;
        this.proteins = proteins;
        this.fats = fats;
        this.carbs = carbs;
    }

    public Meal() {

    }

    public void setTime(String currentSelectedDate){currentSelectedDate = time;}
    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public float getCalories() {
        return calories;
    }

    public float getProteins() {
        return proteins;
    }

    public float getFats() {
        return fats;
    }

    public float getCarbs() {
        return carbs;
    }

    public void updateMeal(String name, String time,  float calories, float proteins, float fats, float carbs) {
        this.name = name;
        this.time = time;
        this.calories = calories;
        this.proteins = proteins;
        this.fats = fats;
        this.carbs = carbs;
    }
}
