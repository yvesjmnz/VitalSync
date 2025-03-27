package com.example.vitalsync;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class GenderActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "GenderPrefs";
    private static final String GENDER_KEY = "SelectedGender";

    private TextView maleText, femaleText;
    private ImageButton nextButton;
    private TextView backText;
    private String selectedGender = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gender);

        maleText = findViewById(R.id.male_button);
        femaleText = findViewById(R.id.female_button);
        nextButton = findViewById(R.id.proceed_button);
        backText = findViewById(R.id.textView);

        loadSelectedGender();

        maleText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedGender = "Male";
                selectGenderOption(maleText);
                saveSelectedGender("Male");
                Toast.makeText(GenderActivity.this, "Selected: Male", Toast.LENGTH_SHORT).show();
            }
        });

        femaleText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedGender = "Female";
                selectGenderOption(femaleText);
                saveSelectedGender("Female");
                Toast.makeText(GenderActivity.this, "Selected: Female", Toast.LENGTH_SHORT).show();
            }
        });

        backText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedGender != null) {
                    Intent intent = new Intent(GenderActivity.this, ActivenessActivity.class);
                    intent.putExtra("selectedGender", selectedGender);
                    startActivity(intent);
                } else {
                    showMessage("Please select a gender before proceeding.");
                }
            }
        });
    }

    private void selectGenderOption(TextView selectedOption) {
        maleText.setBackgroundResource(android.R.color.transparent);
        femaleText.setBackgroundResource(android.R.color.transparent);
        selectedOption.setBackgroundResource(R.drawable.outline);
    }

    private void saveSelectedGender(String gender) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(GENDER_KEY, gender);
        editor.apply();
    }

    private void loadSelectedGender() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedGender = sharedPreferences.getString(GENDER_KEY, null);

        if (savedGender != null) {
            if (savedGender.equals("Male")) {
                selectedGender = "Male";
                selectGenderOption(maleText);
            } else if (savedGender.equals("Female")) {
                selectedGender = "Female";
                selectGenderOption(femaleText);
            }
        }
    }

    private void showMessage(String message) {
        Toast.makeText(GenderActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
