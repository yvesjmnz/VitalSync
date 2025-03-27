package com.example.vitalsync;

public class foodItem {
    private String name;
    private float calories;
    private float proteins;
    private float carbs;
    private float fats;

    public foodItem(String name, float calories, float proteins, float carbs, float fats) {
        this.name = name;
        this.calories = calories;
        this.proteins = proteins;
        this.carbs = carbs;
        this.fats = fats;
    }
    public foodItem(){

    }
    public String getName() {
        return name;
    }

    public float getCalories() {
        return calories;
    }

    public float getProteins() {
        return proteins;
    }

    public float getCarbs() {
        return carbs;
    }

    public float getFats() {
        return fats;
    }
}
