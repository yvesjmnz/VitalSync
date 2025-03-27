package com.example.vitalsync;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText usernameInput, emailInput, passwordInput;
    private Button loginButton;
    private ImageView backButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createaccount);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        usernameInput = findViewById(R.id.username_input);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);
        backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> finish());

        loginButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(CreateAccountActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            } else {
                createAccount(username, email, password);
            }
        });
    }

    private void createAccount(String username, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {

                            Toast.makeText(CreateAccountActivity.this, "Account Created", Toast.LENGTH_SHORT).show();

                            saveUserDetails(user.getUid(), username, email);
                        }
                    } else {
                        Toast.makeText(CreateAccountActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserDetails(String userId, String username, String email) {
        SharedPreferences weightPrefs = getSharedPreferences("WeightPrefs", Context.MODE_PRIVATE);
        SharedPreferences heightPrefs = getSharedPreferences("HeightPrefs", Context.MODE_PRIVATE);
        SharedPreferences agePrefs = getSharedPreferences("AgePrefs", Context.MODE_PRIVATE);
        SharedPreferences genderPrefs = getSharedPreferences("GenderPrefs", Context.MODE_PRIVATE);
        SharedPreferences activityPrefs = getSharedPreferences("ActivityLevelPrefs", Context.MODE_PRIVATE);
        SharedPreferences pfcPrefs = getSharedPreferences("PFCValues", Context.MODE_PRIVATE);

        String weight = weightPrefs.getString("Weight", "");
        String weightUnit = weightPrefs.getBoolean("Unit", true) ? "kg" : "lbs";
        int heightMeters = heightPrefs.getInt("HeightMeters", 0);
        int heightCentimeters = heightPrefs.getInt("HeightCentimeters", 0);
        boolean isMetric = heightPrefs.getBoolean("UnitSystem", true);
        String height = convertHeightToCentimeters(heightMeters, heightCentimeters, isMetric);
        String age = agePrefs.getString("Age", "");
        String gender = genderPrefs.getString("SelectedGender", "");
        String activityLevel = activityPrefs.getString("SelectedActivityLevel", "");

        float calories = pfcPrefs.getFloat("Calories", 0);
        float proteins = pfcPrefs.getFloat("Proteins", 0);
        float fats = pfcPrefs.getFloat("Fats", 0);
        float carbs = pfcPrefs.getFloat("Carbs", 0);

        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("email", email);

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("weight", weight);
        userInfo.put("weight_unit", weightUnit);
        userInfo.put("height", height);
        userInfo.put("age", age);
        userInfo.put("gender", gender);
        userInfo.put("activity_level", activityLevel);

        user.put("user_info", userInfo);

        Map<String, Object> intake = new HashMap<>();
        intake.put("calories", calories);
        intake.put("proteins", proteins);
        intake.put("fats", fats);
        intake.put("carbs", carbs);

        user.put("intake", intake);

        db.collection("users").document(userId).set(user)
                .addOnSuccessListener(aVoid -> {

                    Toast.makeText(CreateAccountActivity.this, "Welcome, " + username + "!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CreateAccountActivity.this, Homepage.class));
                    finish();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(CreateAccountActivity.this, "Failed to save user details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private String convertHeightToCentimeters(int heightMeters, int heightCentimeters, boolean isMetric) {
        float h;
        if (isMetric) {
            // metric input
            h =  heightMeters * 100 + heightCentimeters; // Convert to centimeters
        } else {
            // imperial input
            h =  (heightMeters * 30.48f) + (heightCentimeters * 2.54f); // Convert feet and inches to centimeters
        }
        return String.valueOf(h);
    }
}
