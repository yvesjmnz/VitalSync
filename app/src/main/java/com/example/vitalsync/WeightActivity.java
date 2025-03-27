package com.example.vitalsync;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class WeightActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "WeightPrefs";
    private static final String WEIGHT_KEY = "Weight";
    private static final String UNIT_KEY = "Unit";

    private EditText weightInput;
    private TextView unitToggleButton;
    private ImageButton nextButton;
    private TextView backText;
    private boolean isKg = true;  // default kg

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight);

        weightInput = findViewById(R.id.weight_input);
        unitToggleButton = findViewById(R.id.unit_toggle_button);
        nextButton = findViewById(R.id.proceed_button);
        backText = findViewById(R.id.textView);

        loadPreferences();

        // kg/lbs toggle
        unitToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isKg = !isKg;
                unitToggleButton.setText(isKg ? "kg" : "lbs");
                savePreferences();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String weight = weightInput.getText().toString();
                if (weight.isEmpty()) {
                    Toast.makeText(WeightActivity.this, "Please enter your weight", Toast.LENGTH_SHORT).show();
                } else {
                    String selectedUnit = isKg ? "kg" : "lbs";
                    Intent intent = new Intent(WeightActivity.this, AgeActivity.class);
                    intent.putExtra("weight", weight);
                    intent.putExtra("unit", selectedUnit);
                    startActivity(intent);
                    savePreferences();
                }
            }
        });

        // back
        backText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void savePreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(WEIGHT_KEY, weightInput.getText().toString());
        editor.putBoolean(UNIT_KEY, isKg);
        editor.apply();
    }

    private void loadPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedWeight = sharedPreferences.getString(WEIGHT_KEY, "");
        isKg = sharedPreferences.getBoolean(UNIT_KEY, true);

        weightInput.setText(savedWeight);
        unitToggleButton.setText(isKg ? "kg" : "lbs");
    }
}
