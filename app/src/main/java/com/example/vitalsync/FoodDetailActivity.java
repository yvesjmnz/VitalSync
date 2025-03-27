package com.example.vitalsync;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class FoodDetailActivity extends AppCompatActivity {

    private TextView foodNameTextView, caloriesTextView, proteinsTextView, carbsTextView, fatsTextView;
    private ImageButton backButton;
    private Button saveFoodButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        foodNameTextView = findViewById(R.id.foodNameTextView);
        caloriesTextView = findViewById(R.id.caloriesTextView);
        proteinsTextView = findViewById(R.id.proteinsTextView);
        carbsTextView = findViewById(R.id.carbsTextView);
        fatsTextView = findViewById(R.id.fatsTextView);
        backButton = findViewById(R.id.back_button);
        saveFoodButton = findViewById(R.id.saveFoodButton);
        String foodName = getIntent().getStringExtra("foodName");
        float calories = getIntent().getFloatExtra("calories", -1);
        float proteins = getIntent().getFloatExtra("proteins", -1);
        float carbs = getIntent().getFloatExtra("carbs", -1);
        float fats = getIntent().getFloatExtra("fats", -1);

        backButton.setOnClickListener(v -> finish());

        if (foodName != null && !foodName.isEmpty()) {
            foodNameTextView.setText(foodName);
        } else {
            foodNameTextView.setText("Food Name: Not available");
        }

        caloriesTextView.setText(calories >= 0 ? "Calories: " + calories + " kcal" : "Calories: Not available");
        proteinsTextView.setText(proteins >= 0 ? "Proteins: " + proteins + " g" : "Proteins: Not available");
        carbsTextView.setText(carbs >= 0 ? "Carbs: " + carbs + " g" : "Carbs: Not available");
        fatsTextView.setText(fats >= 0 ? "Fats: " + fats + " g" : "Fats: Not available");

        saveFoodButton.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("foodName", foodName);
            resultIntent.putExtra("calories", calories);
            resultIntent.putExtra("proteins", proteins);
            resultIntent.putExtra("carbs", carbs);
            resultIntent.putExtra("fats", fats);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

    }
}
