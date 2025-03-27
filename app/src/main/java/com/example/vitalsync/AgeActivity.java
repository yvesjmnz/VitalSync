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

public class AgeActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "AgePrefs";
    private static final String AGE_KEY = "Age";

    private EditText ageInput;
    private ImageButton nextButton;
    private TextView backText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_age);

        ageInput = findViewById(R.id.age_input);
        nextButton = findViewById(R.id.proceed_button);
        backText = findViewById(R.id.textView);

        loadAgePreference();

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String age = ageInput.getText().toString();
                if (age.isEmpty()) {
                    Toast.makeText(AgeActivity.this, "Please enter your age", Toast.LENGTH_SHORT).show();
                } else {
                    // Save age to preferences and proceed to the next activity
                    saveAgePreference(age);
                    Intent intent = new Intent(AgeActivity.this, PFCActivity.class);
                    intent.putExtra("age", age);
                    startActivity(intent);
                }
            }
        });

        // back button
        backText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void saveAgePreference(String age) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(AGE_KEY, age);
        editor.apply();
    }

    private void loadAgePreference() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedAge = sharedPreferences.getString(AGE_KEY, "");
        ageInput.setText(savedAge);
    }
}
