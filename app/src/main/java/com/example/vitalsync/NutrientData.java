package com.example.vitalsync;

public class NutrientData {
    private float proteins;
    private float fats;
    private float carbs;
    private float calories;

    public NutrientData() {
    }

    public NutrientData(float proteins, float fats, float carbs, float calories) {
        this.proteins = proteins;
        this.fats = fats;
        this.carbs = carbs;
        this.calories = calories;
    }

    public float getProteins() {
        return proteins;
    }

    public void setProteins(float proteins) {
        this.proteins = proteins;
    }

    public float getFats() {
        return fats;
    }

    public void setFats(float fats) {
        this.fats = fats;
    }

    public float getCarbs() {
        return carbs;
    }

    public void setCarbs(float carbs) {
        this.carbs = carbs;
    }

    public float getCalories() {
        return calories;
    }

    public void setCalories(float calories) {
        this.calories = calories;
    }
}

