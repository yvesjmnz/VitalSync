package com.example.vitalsync;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MeActivity extends AppCompatActivity {

    private TextView goalText, ageText, heightText, weightText, genderText, lifestyleText;
    private Button saveButton;
    private ImageButton backButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private Map<String, Object> updatedData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        goalText = findViewById(R.id.goalText);
        ageText = findViewById(R.id.ageText);
        heightText = findViewById(R.id.heightText);
        weightText = findViewById(R.id.weightText);
        genderText = findViewById(R.id.genderText);
        lifestyleText = findViewById(R.id.lifestyleText);
        saveButton = findViewById(R.id.saveButton);
        backButton = findViewById(R.id.back_button_me);

        updatedData = new HashMap<>();

        loadUserData();

        goalText.setOnClickListener(v -> showSelectionDialog("Edit Goal", goalText, "goal", new String[]{"Lose weight", "Keep weight", "Gain weight"}));
        ageText.setOnClickListener(v -> showEditDialog("Edit Age", ageText, "age"));
        heightText.setOnClickListener(v -> showEditDialog("Edit Height", heightText, "height"));
        weightText.setOnClickListener(v -> showEditDialog("Edit Weight", weightText, "weight"));
        lifestyleText.setOnClickListener(v -> showSelectionDialog("Edit Lifestyle", lifestyleText, "activity_level", new String[]{"Sedentary", "Low Active", "Active", "Very Active"}));

        backButton.setOnClickListener(v -> finish());

        saveButton.setOnClickListener(v -> saveChangesToFirestore());
    }

    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            db.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Map<String, Object> userInfo = (Map<String, Object>) documentSnapshot.get("user_info");

                            if (userInfo != null) {
                                String goal = userInfo.get("goal") != null ? userInfo.get("goal").toString() : "Keep weight";
                                goalText.setText(goal);
                                updatedData.put("goal", goal);

                                String age = userInfo.get("age") != null ? userInfo.get("age").toString() + " years" : "N/A";
                                ageText.setText(age);
                                updatedData.put("age", userInfo.get("age"));

                                String height = userInfo.get("height") != null ? userInfo.get("height").toString() : "N/A";
                                heightText.setText(height);
                                updatedData.put("height", height);

                                String weight = userInfo.get("weight") != null ? userInfo.get("weight").toString() : "N/A";
                                weightText.setText(weight + " kg");
                                updatedData.put("weight", weight);

                                String gender = userInfo.get("gender") != null ? userInfo.get("gender").toString() : "N/A";
                                genderText.setText(gender);
                                updatedData.put("gender", gender);

                                String activityLevel = userInfo.get("activity_level") != null ? userInfo.get("activity_level").toString() : "N/A";
                                lifestyleText.setText(activityLevel);
                                updatedData.put("activity_level", activityLevel);
                            }
                        } else {
                            Toast.makeText(MeActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(MeActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show());
        }
    }

    private void showEditDialog(String title, TextView textViewToUpdate, String fieldKey) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MeActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.edit_value, null);
        builder.setView(dialogView);

        EditText valueInput = dialogView.findViewById(R.id.editValueInput);
        valueInput.setText(textViewToUpdate.getText().toString().replace(" kg", "").replace(" years", ""));

        builder.setTitle(title)
                .setPositiveButton("Done", (dialog, id) -> {
                    String newValue = valueInput.getText().toString();
                    textViewToUpdate.setText(fieldKey.equals("weight") ? newValue + " kg" : newValue);
                    updatedData.put(fieldKey, newValue);
                    Toast.makeText(MeActivity.this, title + " updated to: " + newValue, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showSelectionDialog(String title, TextView textViewToUpdate, String fieldKey, String[] options) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MeActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.select_value, null);
        builder.setView(dialogView);

        RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroup);
        RadioButton option1 = dialogView.findViewById(R.id.radio_option_1);
        RadioButton option2 = dialogView.findViewById(R.id.radio_option_2);
        RadioButton option3 = dialogView.findViewById(R.id.radio_option_3);

        option1.setText(options[0]);
        option2.setText(options[1]);
        option3.setText(options[2]);

        builder.setTitle(title);

        AlertDialog alertDialog = builder.create();

        Button doneButton = dialogView.findViewById(R.id.doneButton);
        doneButton.setOnClickListener(v -> {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            if (selectedId != -1) {
                RadioButton selectedOption = dialogView.findViewById(selectedId);
                String selectedText = selectedOption.getText().toString();
                textViewToUpdate.setText(selectedText);
                updatedData.put(fieldKey, selectedText);
                Toast.makeText(MeActivity.this, title + " updated to: " + selectedText, Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void saveChangesToFirestore() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            db.collection("users").document(userId)
                    .update("user_info", updatedData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(MeActivity.this, "Changes saved successfully!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(MeActivity.this, "Failed to save changes", Toast.LENGTH_SHORT).show());
        }
    }
}
