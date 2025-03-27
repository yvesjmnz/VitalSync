package com.example.vitalsync;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FoodSearchActivity extends AppCompatActivity {

    private EditText searchEditText;
    private ImageButton backButton;
    private TextView emptyTextView;
    private RecyclerView foodRecyclerView;
    private SearchAdapter foodAdapter;
    private List<NutritionixResponse.FoodItem> foodItems;
    private ActivityResultLauncher<Intent> foodDetailLauncher;
    private NutritionixApiService apiService;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_search);

        searchEditText = findViewById(R.id.searchEditText);
        backButton = findViewById(R.id.back_button);
        emptyTextView = findViewById(R.id.emptyTextView);
        foodRecyclerView = findViewById(R.id.foodRecyclerView);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://trackapi.nutritionix.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(NutritionixApiService.class);

        foodRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        foodItems = new ArrayList<>();
        foodAdapter = new SearchAdapter(this, foodItems, false);
        foodRecyclerView.setAdapter(foodAdapter);

        backButton.setOnClickListener(v -> finish());

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }

                searchRunnable = () -> {
                    String query = charSequence.toString().trim();
                    if (query.length() >= 3) {
                        searchFood(query);
                    } else {
                        foodItems.clear();
                        foodAdapter.updateFoodList(foodItems);
                        updateEmptyView();
                    }
                };
                handler.postDelayed(searchRunnable, 500);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        foodDetailLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        String foodName = data.getStringExtra("foodName");
                        float calories = data.getFloatExtra("calories", -1);
                        float proteins = data.getFloatExtra("proteins", -1);
                        float carbs = data.getFloatExtra("carbs", -1);
                        float fats = data.getFloatExtra("fats", -1);

                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("foodName", foodName);
                        resultIntent.putExtra("calories", calories);
                        resultIntent.putExtra("proteins", proteins);
                        resultIntent.putExtra("carbs", carbs);
                        resultIntent.putExtra("fats", fats);
                        setResult(RESULT_OK, resultIntent);

                        finish();
                    }
                }
        );


        foodAdapter.setOnItemClickListener(foodItem -> {
            NutritionixRequest request = new NutritionixRequest(foodItem.getFoodName());
            apiService.getNutrients(request).enqueue(new Callback<NutritionixResponse>() {
                @Override
                public void onResponse(Call<NutritionixResponse> call, Response<NutritionixResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        NutritionixResponse.FoodItem detailedFood = response.body().getFoods().get(0);

                        Intent intent = new Intent(FoodSearchActivity.this, FoodDetailActivity.class);
                        intent.putExtra("foodName", detailedFood.getFoodName());
                        intent.putExtra("calories", detailedFood.getCalories());
                        intent.putExtra("proteins", detailedFood.getProteins());
                        intent.putExtra("carbs", detailedFood.getCarbs());
                        intent.putExtra("fats", detailedFood.getFats());
                        foodDetailLauncher.launch(intent);
                    } else {
                        Log.d("FoodSearchActivity", "Error fetching food details: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<NutritionixResponse> call, Throwable t) {
                    Log.e("FoodSearchActivity", "Error fetching food details", t);
                }
            });
        });

        updateEmptyView();
    }

    private void searchFood(String query) {
        Call<NutritionixResponse> call = apiService.searchInstant(query);

        call.enqueue(new Callback<NutritionixResponse>() {
            @Override
            public void onResponse(Call<NutritionixResponse> call, Response<NutritionixResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    foodItems = response.body().getCommonFoods();
                    if (foodItems != null && !foodItems.isEmpty()) {
                        foodAdapter.updateFoodList(foodItems);
                        updateEmptyView();
                    } else {
                        emptyTextView.setText("No results found.");
                        updateEmptyView();
                    }
                } else {
                    Log.d("FoodSearchActivity", "Error: " + response.code() + ", " + response.errorBody());
                    emptyTextView.setText("Failed to load data.");
                    updateEmptyView();
                }
            }

            @Override
            public void onFailure(Call<NutritionixResponse> call, Throwable t) {
                Log.e("FoodSearchActivity", "API call failed", t);
                emptyTextView.setText("Failed to load data.");
                updateEmptyView();
            }
        });
    }

    private void updateEmptyView() {
        if (foodItems.isEmpty()) {
            foodRecyclerView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
        } else {
            foodRecyclerView.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);
        }
    }
}
