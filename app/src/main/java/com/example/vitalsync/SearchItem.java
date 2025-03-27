package com.example.vitalsync;

public class SearchItem {
    private String name;
    private float calories;
    private float proteins;
    private float carbs;
    private float fats;

    public SearchItem(String name, float calories, float proteins, float carbs, float fats) {
        this.name = name;
        this.calories = calories;
        this.proteins = proteins;
        this.carbs = carbs;
        this.fats = fats;
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
