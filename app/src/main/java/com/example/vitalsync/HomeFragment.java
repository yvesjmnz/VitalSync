package com.example.vitalsync;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;



public class HomeFragment extends Fragment implements MealAdapter.OnMealClickListener, MealAdapter.OnMealLongClickListener {
    private TextView tvProtein, tvFats, tvCarbs, tvCalories;
    private ProgressBar progressBarProteins, progressBarFats, progressBarCarbs, progressBarCalories;
    private ActivityResultLauncher<Intent> mealInputLauncher;
    private RecyclerView recyclerView;
    private MealAdapter mealAdapter;
    private List<Meal> mealList;
    private View waterBackg;
    private TextView waterPercent;
    private int waterHeight;
    private static final int MAX_MARGIN_TOP_DP = 478;
    private static final int MIN_MARGIN_TOP_DP = 345;
    private static final int GLASS_HEIGHT_DP = (MAX_MARGIN_TOP_DP - MIN_MARGIN_TOP_DP) / 8;
    private static final double DAILY_GOAL_LITERS = 2.8;
    private static final double GLASS_VOLUME_LITERS = 0.35;
    private int glassesOfWater;
    private TextView litersWaterTextView;
    private TextView lastTimeTextView;
    private TextView username;
    private TextView selectedDate;
    private int editPosition = -1;
    private float TARGET_PROTEINS = 150;
    private float TARGET_FATS = 50;
    private float TARGET_CARBS = 190;
    private float TARGET_CALORIES = 2000;
    private float currentProteins = 0;
    private float currentFats = 0;
    private float currentCarbs = 0;
    private float currentCalories = 0;
    private boolean mealsInitialized = false;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private static final String SHARED_PREFS = "dailyBitePrefs";
    private static final String MEALS_KEY = "meals";
    private static final String WATER_KEY = "water";
    private static final String NUTRIENTS_KEY = "nutrients";
    private SharedPreferences sharedPreferences;
    private String currentDate;
    private Gson gson;
    private String userId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // if user is not logged in/authenticated by firebase redirect to login/signup
            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
            return view;
        }

        userId = currentUser.getUid();
        sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        gson = new Gson();


        mDatabase = FirebaseDatabase.getInstance().getReference();


        username = view.findViewById(R.id.username);
        loadUsername();
        username.setOnClickListener(v -> navigateToUserProfile());

        ImageView calendarIcon = view.findViewById(R.id.calendar_icon);
        calendarIcon.setOnClickListener(v -> openCalendar());

        ImageView plusIconMeal = view.findViewById(R.id.plus_icon_meal);
        plusIconMeal.setOnClickListener(v -> navigateToMealInputWithoutDate());

        waterBackg = view.findViewById(R.id.water_backg);
        waterPercent = view.findViewById(R.id.water_drank);
        glassesOfWater = 0;
        litersWaterTextView = view.findViewById(R.id.liters_water);
        updateWaterDisplay();
        lastTimeTextView = view.findViewById(R.id.last_time_1);

        ImageView plusIcon = view.findViewById(R.id.plus_icon);
        ImageView minusIcon = view.findViewById(R.id.minus_icon);
        plusIcon.setOnClickListener(v -> addGlass());
        minusIcon.setOnClickListener(v -> minusGlass());

        tvProtein = view.findViewById(R.id.tv_protein);
        tvFats = view.findViewById(R.id.tv_fats);
        tvCarbs = view.findViewById(R.id.tv_carbs);
        tvCalories = view.findViewById(R.id.tv_calories);

        progressBarProteins = view.findViewById(R.id.progressBarDeterminate_proteins);
        progressBarFats = view.findViewById(R.id.progressBarDeterminate_fats);
        progressBarCarbs = view.findViewById(R.id.progressBarDeterminate_Carbs);
        progressBarCalories = view.findViewById(R.id.progressBarDeterminate_calories);

        selectedDate = view.findViewById(R.id.date);
        currentDate = getCurrentDate();
        loadDataForDate(currentDate);
        mealAdapter = new MealAdapter(mealList, this, this);
        recyclerView = view.findViewById(R.id.recyclerView_meals);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mealAdapter);


        mealInputLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        String mealName = result.getData().getStringExtra("MEAL_NAME");
                        float mealCalories = Float.parseFloat(result.getData().getStringExtra("MEAL_CALORIES"));
                        float mealProteins = Float.parseFloat(result.getData().getStringExtra("MEAL_PROTEINS"));
                        float mealFats = Float.parseFloat(result.getData().getStringExtra("MEAL_FATS"));
                        float mealCarbs = Float.parseFloat(result.getData().getStringExtra("MEAL_CARBS"));
                        String currentTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());

                        if (editPosition != -1) {
                            Meal existingMeal = mealList.get(editPosition);
                            existingMeal.updateMeal(mealName, currentTime, mealCalories, mealProteins, mealFats, mealCarbs);
                            mealAdapter.notifyItemChanged(editPosition);
                            editPosition = -1;
                        } else {
                            Meal newMeal = new Meal(mealName, currentTime, mealCalories, mealProteins, mealFats, mealCarbs);
                            addMeal(newMeal);
                            mealAdapter.notifyItemInserted(mealList.size() - 1);
                        }
                        initializeMeals();
                        updateNutrientViews();
                    }
                }
        );

        fetchIntakeTargets();
        if (!mealsInitialized) {
            initializeMeals();
            mealsInitialized = true;
        }
        updateNutrientViews();

        return view;
    }

    private void fetchIntakeTargets() {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> intake = (Map<String, Object>) documentSnapshot.get("intake");
                        if (intake != null) {
                            TARGET_PROTEINS = ((Number) intake.get("proteins")).floatValue();
                            TARGET_FATS = ((Number) intake.get("fats")).floatValue();
                            TARGET_CARBS = ((Number) intake.get("carbs")).floatValue();
                            TARGET_CALORIES = ((Number) intake.get("calories")).floatValue();
                        }
                        updateNutrientViews();
                    } else {
                        Log.d("HomeFragment", "No intake data found for this user.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("HomeFragment", "Error fetching intake data", e);
                    Toast.makeText(getContext(), "Failed to load intake targets", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateNutrientViews() {
        tvProtein.setText(String.format(Locale.getDefault(), "%.1f / %.1f", currentProteins, TARGET_PROTEINS));
        progressBarProteins.setProgress((int) (currentProteins / TARGET_PROTEINS * 100));

        tvFats.setText(String.format(Locale.getDefault(), "%.1f / %.1f", currentFats, TARGET_FATS));
        progressBarFats.setProgress((int) (currentFats / TARGET_FATS * 100));

        tvCarbs.setText(String.format(Locale.getDefault(), "%.1f / %.1f", currentCarbs, TARGET_CARBS));
        progressBarCarbs.setProgress((int) (currentCarbs / TARGET_CARBS * 100));

        tvCalories.setText(String.format(Locale.getDefault(), "%.1f / %.1f", currentCalories, TARGET_CALORIES));
        progressBarCalories.setProgress((int) (currentCalories / TARGET_CALORIES * 100));
    }

    private void loadUsername() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            db.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String fetchedUsername = documentSnapshot.getString("username");
                            if (fetchedUsername != null) {
                                // Set the username in the TextView
                                username.setText(fetchedUsername);
                            } else {
                                Log.d("HomeFragment", "Username not found in Firestore.");
                            }
                        } else {
                            Log.d("HomeFragment", "No user document found.");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.d("HomeFragment", "Error fetching username from Firestore", e);
                    });
        } else {
            Log.d("HomeFragment", "No current user found.");
        }
    }


    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }
    private void updateSelectedDate() {
        selectedDate.setText(currentDate);
    }

    private void openCalendar() {
        Calendar calendar = Calendar.getInstance(); // Current date
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getActivity(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    Calendar selectedCalendar = Calendar.getInstance();
                    selectedCalendar.set(selectedYear, selectedMonth, selectedDay);

                    if (isSameDay(selectedCalendar, calendar)) {
                        saveSelectedDate(getCurrentDate());
                        loadDataForDate(getCurrentDate());
                        currentDate = getCurrentDate();
                    } else {
                        String newDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                        saveSelectedDate(newDate);
                        loadDataForDate(newDate);
                        currentDate = newDate;
                    }

                    updateSelectedDate();
                },
                year, month, day
        );

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        datePickerDialog.show();
    }

    private boolean isSameDay(Calendar c1, Calendar c2) {
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) &&
                c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);
    }



    private void saveSelectedDate(String date) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("SELECTED_DATE", date);
        editor.apply();
    }


    private void loadDataForDate(String date) {
        String nutrientsJson = sharedPreferences.getString(NUTRIENTS_KEY + "_" + date + "_" + userId, null);
        if (nutrientsJson != null) {
            NutrientData nutrientData = gson.fromJson(nutrientsJson, NutrientData.class);
            currentProteins = nutrientData.getProteins();
            currentFats = nutrientData.getFats();
            currentCarbs = nutrientData.getCarbs();
            currentCalories = nutrientData.getCalories();
        } else {
            currentProteins = 0;
            currentFats = 0;
            currentCarbs = 0;
            currentCalories = 0;
        }

        int waterConsumed = sharedPreferences.getInt(WATER_KEY + "_" + date + "_" + userId,  0);
        glassesOfWater = waterConsumed;

        updateNutrientViews();
        updateWaterDisplay();
        loadMealsForDate(date);
    }

    private void saveDataForDate(String date) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String mealsJson = gson.toJson(mealList);
        editor.putString(MEALS_KEY + "_" + date + "_" + userId, mealsJson);

        NutrientData nutrientData = new NutrientData(currentProteins, currentFats, currentCarbs, currentCalories);
        String nutrientsJson = gson.toJson(nutrientData);
        editor.putString(NUTRIENTS_KEY + "_" + date + "_" + userId, nutrientsJson);

        editor.putInt(WATER_KEY + "_" + date + "_" + userId, glassesOfWater);

        editor.apply();

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("meals", mealList);
        dataMap.put("nutrients", nutrientData);
        dataMap.put("waterIntake", glassesOfWater);

        mDatabase.child("users")
                .child(userId)
                .child("data")
                .child(date)
                .setValue(dataMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("FirebaseSave", "Data saved successfully for date: " + date);
                    } else {
                        Log.e("FirebaseSave", "Failed to save data for date: " + date, task.getException());
                    }
                });
    }


    private void navigateToMealInputWithoutDate() {
        Intent intent = new Intent(getActivity(), meal_input.class);
        intent.putExtra("CURRENT_DATE", currentDate);
        mealInputLauncher.launch(intent);
    }

    private void updateWaterDisplay() {
        float waterHeightDp = glassesOfWater * GLASS_HEIGHT_DP;
        float marginTopDp = MAX_MARGIN_TOP_DP - (glassesOfWater * GLASS_HEIGHT_DP);

        int waterHeightPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, waterHeightDp, getResources().getDisplayMetrics());
        int marginTopPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginTopDp, getResources().getDisplayMetrics());

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) waterBackg.getLayoutParams();
        params.height = waterHeightPx;
        params.topMargin = marginTopPx;
        waterBackg.setLayoutParams(params);
        waterPercent.setText(glassesOfWater + " Cups");

        double litersConsumed = glassesOfWater * GLASS_VOLUME_LITERS;
        litersWaterTextView.setText(String.format("%.2f / %.1f Liters", litersConsumed, DAILY_GOAL_LITERS));
    }

    private void addGlass() {
        if (glassesOfWater < 8) {
            glassesOfWater++;
            waterHeight += GLASS_HEIGHT_DP;
            updateWaterDisplay();
            saveDataForDate(currentDate);
            updateLastTime();
        }
    }

    private void minusGlass() {
        if (glassesOfWater > 0) {
            glassesOfWater--;
            waterHeight -= GLASS_HEIGHT_DP;
            updateWaterDisplay();
            saveDataForDate(currentDate);
            updateLastTime();
        }
    }

    private void updateLastTime() {
        String currentTime = getCurrentTime();
        lastTimeTextView.setText(currentTime);
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return "Last added: " + sdf.format(new Date());
    }

    private void navigateToUserProfile() {
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        startActivity(intent);
    }

    @Override
    public void onMealClick(Meal meal) {
        editPosition = mealList.indexOf(meal);

        if (editPosition != -1) { // Check if the meal exists in the list
            Intent intent = new Intent(getActivity(), meal_input.class);
            intent.putExtra("MEAL_NAME", meal.getName());
            intent.putExtra("MEAL_CALORIES", String.valueOf(meal.getCalories()));
            intent.putExtra("MEAL_PROTEINS", String.valueOf(meal.getProteins()));
            intent.putExtra("MEAL_FATS", String.valueOf(meal.getFats()));
            intent.putExtra("MEAL_CARBS", String.valueOf(meal.getCarbs()));
            intent.putExtra("CURRENT_DATE", currentDate);
            mealInputLauncher.launch(intent);
        } else {
            Log.e("HomeFragment", "Meal not found in mealList. Position invalid.");
            Toast.makeText(getContext(), "Error: Meal not found", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onMealLongClick(int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Meal")
                .setMessage("Are you sure you want to delete this meal?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Meal mealToDelete = mealList.get(position);
                    mealList.remove(position);
                    deleteMeal(mealToDelete);
                    mealAdapter.notifyItemRemoved(position);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void initializeMeals() {
        currentProteins = 0;
        currentFats = 0;
        currentCarbs = 0;
        currentCalories = 0;

        for (Meal meal : mealList) {
            currentProteins += meal.getProteins();
            currentFats += meal.getFats();
            currentCarbs += meal.getCarbs();
            currentCalories += meal.getCalories();
        }
        updateNutrientViews();
    }

    private void saveMeals() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String mealListJson = gson.toJson(mealList);
        editor.putString(MEALS_KEY + "_" + currentDate + "_" + userId, mealListJson);
        editor.apply();
    }

    private void loadMealsForDate(String date) {
        String mealListJson = sharedPreferences.getString(MEALS_KEY + "_" + date + "_" + userId, null);
        if (mealListJson != null) {
            Type type = new TypeToken<List<Meal>>() {}.getType();
            mealList = gson.fromJson(mealListJson, type);
        } else {
            mealList = new ArrayList<>();
        }

        if (mealAdapter != null) {
            mealAdapter.setMealList(mealList);
        }

        initializeMeals();
    }


    private void editMeal(Meal oldMeal, Meal newMeal) {
        currentProteins = currentProteins - oldMeal.getProteins() + newMeal.getProteins();
        currentFats = currentFats - oldMeal.getFats() + newMeal.getFats();
        currentCarbs = currentCarbs - oldMeal.getCarbs() + newMeal.getCarbs();
        currentCalories = currentCalories - oldMeal.getCalories() + newMeal.getCalories();
        updateNutrientViews();

        int index = mealList.indexOf(oldMeal);
        if (index != -1) {
            mealList.set(index, newMeal);
            saveMeals();
            saveDataForDate(currentDate);
        }
    }



    private void deleteMeal(Meal meal) {
        currentProteins -= meal.getProteins();
        currentFats -= meal.getFats();
        currentCarbs -= meal.getCarbs();
        currentCalories -= meal.getCalories();

        mealList.remove(meal);
        saveMeals();
        saveDataForDate(currentDate);
        updateNutrientViews();
    }

    private void addMeal(Meal meal) {
        currentProteins += meal.getProteins();
        currentFats += meal.getFats();
        currentCarbs += meal.getCarbs();
        currentCalories += meal.getCalories();

        mealList.add(meal);
        saveMeals();
        saveDataForDate(currentDate);
        updateNutrientViews();
    }

}
