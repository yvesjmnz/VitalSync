package com.example.vitalsync;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class foodAdapter extends RecyclerView.Adapter<foodAdapter.FoodViewHolder> {

    private Context context;
    private List<foodItem> foodList;
    private boolean showCalories;
    private OnFoodItemDeletedListener deleteListener;

    public foodAdapter(Context context, List<foodItem> foodList, boolean showCalories, OnFoodItemDeletedListener deleteListener) {
        this.context = context;
        this.foodList = foodList;
        this.showCalories = showCalories;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.food_item, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        foodItem foodItem = foodList.get(position);
        holder.foodNameTextView.setText(foodItem.getName());
        Log.d("Flag", String.valueOf(showCalories));
        if (showCalories) {
            holder.foodCaloriesTextView.setVisibility(View.VISIBLE);
            holder.foodCaloriesTextView.setText(String.format("%.1f Cal", foodItem.getCalories()));
            holder.foodFatsTextView.setVisibility(View.VISIBLE);
            holder.foodFatsTextView.setText(String.format("Fats: %.2f g ", foodItem.getFats()));
            holder.foodCarbsTextView.setVisibility(View.VISIBLE);
            holder.foodCarbsTextView.setText(String.format("Carbs: %.2f g | ", foodItem.getCarbs()));
            holder.foodProteinsTextView.setVisibility(View.VISIBLE);
            holder.foodProteinsTextView.setText(String.format("Proteins: %.2f g | ", foodItem.getProteins()));
        }

        holder.itemView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Food Item")
                    .setMessage("Are you sure you want to delete this item?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        foodList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, foodList.size());

                        if (deleteListener != null) {
                            deleteListener.onFoodItemDeleted(position);
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();

            return true;
        });


        holder.deleteButton.setOnClickListener(v -> {
            foodList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, foodList.size());

            if (deleteListener != null) {
                deleteListener.onFoodItemDeleted(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public List<foodItem> getFoodList() {
        return foodList;
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView foodCarbsTextViewSpacer,foodProteinsTextViewSpacer, foodCarbsTextView, foodProteinsTextView, foodFatsTextView ;
        TextView foodNameTextView;
        TextView foodCaloriesTextView;
        ImageButton deleteButton;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            foodNameTextView = itemView.findViewById(R.id.foodNameTextView);
            foodCaloriesTextView = itemView.findViewById(R.id.foodCaloriesTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            foodCarbsTextView = itemView.findViewById(R.id.foodCarbsTextView);
            foodProteinsTextView = itemView.findViewById(R.id.foodProteinsTextView);
            foodFatsTextView = itemView.findViewById(R.id.foodFatsTextView);
        }
    }

    public interface OnFoodItemDeletedListener {
        void onFoodItemDeleted(int position);
    }
}
