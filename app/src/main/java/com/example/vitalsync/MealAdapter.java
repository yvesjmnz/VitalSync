package com.example.vitalsync;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {

    private List<Meal> mealList;
    private OnMealClickListener listener;
    private OnMealLongClickListener longClickListener;

    public MealAdapter(List<Meal> mealList, OnMealClickListener listener, OnMealLongClickListener longClickListener) {
        this.mealList = mealList;
        this.listener = listener;
        this.longClickListener = longClickListener;
    }

    public interface OnMealClickListener {
        void onMealClick(Meal meal);
    }

    public interface OnMealLongClickListener {
        void onMealLongClick(int position);
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_search, parent, false);
        return new MealViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        Meal meal = mealList.get(position);
        holder.bind(meal);
    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }

    public class MealViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout mealLayout;
        private TextView mealName, mealTime, mealCalories;
        private ImageView mealClock;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);

            mealLayout = itemView.findViewById(R.id.meal_item_layout);
            mealName = itemView.findViewById(R.id.meal_name);
            mealTime = itemView.findViewById(R.id.meal_time);
            mealCalories = itemView.findViewById(R.id.meal_calories);
            mealClock = itemView.findViewById(R.id.clock_meal);

            mealLayout.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onMealClick(mealList.get(position));
                }
            });

            mealLayout.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    longClickListener.onMealLongClick(position);
                }
                return true;
            });
        }

        public void bind(Meal meal) {
            mealName.setText(meal.getName());
            mealTime.setText(meal.getTime());
            mealCalories.setText(String.valueOf(meal.getCalories()));
        }
    }
    public void setMealList(List<Meal> newMealList) {
        this.mealList = newMealList;
        notifyDataSetChanged();  // notify adapter about data change
    }
}
