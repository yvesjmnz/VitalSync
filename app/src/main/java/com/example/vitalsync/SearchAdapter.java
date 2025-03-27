package com.example.vitalsync;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.FoodViewHolder> {

    private final Context context;
    private List<NutritionixResponse.FoodItem> foodList;
    private final boolean showCalories;
    private OnItemClickListener listener;

    public SearchAdapter(Context context, List<NutritionixResponse.FoodItem> foodList, boolean showCalories) {
        this.context = context;
        this.foodList = foodList;
        this.showCalories = showCalories;
    }

    public interface OnItemClickListener {
        void onItemClick(NutritionixResponse.FoodItem foodItem);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.food_item, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        NutritionixResponse.FoodItem foodItem = foodList.get(position);
        holder.foodNameTextView.setText(foodItem.getFoodName());

        if (showCalories) {
            holder.foodCaloriesTextView.setVisibility(View.VISIBLE);
            holder.foodCaloriesTextView.setText(foodItem.getCalories() + " Calories");
        } else {
            holder.foodCaloriesTextView.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(foodItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public void updateFoodList(List<NutritionixResponse.FoodItem> newFoodList) {
        this.foodList = newFoodList;
        notifyDataSetChanged();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView foodNameTextView;
        TextView foodCaloriesTextView;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            foodNameTextView = itemView.findViewById(R.id.foodNameTextView);
            foodCaloriesTextView = itemView.findViewById(R.id.foodCaloriesTextView);
        }
    }
}
