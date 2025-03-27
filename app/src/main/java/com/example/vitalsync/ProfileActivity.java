package com.example.vitalsync;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private TextView profileTitle, profileEmail, goalValue, calorieIntake;
    private ImageButton backButton, toProfileButton;
    private ImageView userImage;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        backButton = findViewById(R.id.backbutton2);
        profileTitle = findViewById(R.id.textView3);
        profileEmail = findViewById(R.id.textView4);
        goalValue = findViewById(R.id.textView5);
        calorieIntake = findViewById(R.id.CalorieCount2);
        toProfileButton = findViewById(R.id.toProfile);

        loadUserProfile();

        backButton.setOnClickListener(v -> finish());

        findViewById(R.id.ProfileButtonContainer).setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MeActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.CalorieDisplayReport).setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, CalorieIntakeActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.Logout).setOnClickListener(v -> {
            // sign out user from firebase
            mAuth.signOut();

            // sign out user from google as well
            mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
                // Update SharedPreferences to reflect the logout state
                sharedPreferences.edit().putBoolean("isLoggedIn", false).apply();

                // navigate back to the login screen (MainActivity)
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear the back stack
                startActivity(intent);
                finish(); // Close the ProfileActivity
            });
        });
    }

    // Load user's profile information and calorie intake from firestore
    private void loadUserProfile() {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String username = documentSnapshot.getString("username");
                String email = documentSnapshot.getString("email");

                Double calories = documentSnapshot.getDouble("intake.calories");

                if (username != null) {
                    profileTitle.setText(username);
                }
                if (email != null) {
                    profileEmail.setText(email);
                }
                if (calories != null) {
                    calorieIntake.setText(String.format(Locale.getDefault(), "%.0f cal", calories));
                }
            } else {
                Log.d("ProfileActivity", "No such document");
            }
        }).addOnFailureListener(e -> Log.d("ProfileActivity", "Error fetching document", e));
    }
}
