package com.example.vitalsync;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "NutritionTracker.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_GENERAL = "General";
    private static final String TABLE_MEALS = "Meals";
    private static final String TABLE_FOODS = "Foods";

    private static final String COLUMN_USERNAME = "Username";
    private static final String COLUMN_DATE = "Date";
    private static final String COLUMN_TOTAL_CALORIES = "Total_Calories";
    private static final String COLUMN_TOTAL_PROTEINS = "Total_Proteins";
    private static final String COLUMN_TOTAL_FATS = "Total_Fats";
    private static final String COLUMN_TOTAL_CARBS = "Total_Carbs";
    private static final String COLUMN_GLASSES_WATER = "Glasses_Water";

    private static final String COLUMN_MEAL_ID = "Meal_ID";
    private static final String COLUMN_TIME = "Time";
    private static final String COLUMN_MEAL_NAME = "Meal_Name";

    private static final String COLUMN_FOOD_ID = "Food_ID";
    private static final String COLUMN_FOOD_NAME = "Food_Name";
    private static final String COLUMN_CALORIES = "Calories";
    private static final String COLUMN_PROTEINS = "Proteins";
    private static final String COLUMN_FATS = "Fats";
    private static final String COLUMN_CARBS = "Carbs";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_GENERAL + " (" +
                COLUMN_USERNAME + " TEXT NOT NULL, " +
                COLUMN_DATE + " TEXT NOT NULL, " +
                COLUMN_TOTAL_CALORIES + " REAL DEFAULT 0, " +
                COLUMN_TOTAL_PROTEINS + " REAL DEFAULT 0, " +
                COLUMN_TOTAL_FATS + " REAL DEFAULT 0, " +
                COLUMN_TOTAL_CARBS + " REAL DEFAULT 0, " +
                COLUMN_GLASSES_WATER + " INTEGER DEFAULT 0, " +
                "PRIMARY KEY (" + COLUMN_USERNAME + ", " + COLUMN_DATE + "))");

        db.execSQL("CREATE TABLE " + TABLE_MEALS + " (" +
                COLUMN_MEAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT NOT NULL, " +
                COLUMN_DATE + " TEXT NOT NULL, " +
                COLUMN_TIME + " TEXT NOT NULL, " +
                COLUMN_MEAL_NAME + " TEXT NOT NULL, " +
                COLUMN_TOTAL_CALORIES + " REAL DEFAULT 0, " +
                COLUMN_TOTAL_PROTEINS + " REAL DEFAULT 0, " +
                COLUMN_TOTAL_FATS + " REAL DEFAULT 0, " +
                COLUMN_TOTAL_CARBS + " REAL DEFAULT 0, " +
                "FOREIGN KEY (" + COLUMN_USERNAME + ", " + COLUMN_DATE + ") REFERENCES " +
                TABLE_GENERAL + " (" + COLUMN_USERNAME + ", " + COLUMN_DATE + ") ON DELETE CASCADE)");

        db.execSQL("CREATE TABLE " + TABLE_FOODS + " (" +
                COLUMN_FOOD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_MEAL_ID + " INTEGER NOT NULL, " +
                COLUMN_FOOD_NAME + " TEXT NOT NULL, " +
                COLUMN_CALORIES + " REAL NOT NULL, " +
                COLUMN_PROTEINS + " REAL NOT NULL, " +
                COLUMN_FATS + " REAL NOT NULL, " +
                COLUMN_CARBS + " REAL NOT NULL, " +
                "FOREIGN KEY (" + COLUMN_MEAL_ID + ") REFERENCES " + TABLE_MEALS + " (" + COLUMN_MEAL_ID + ") ON DELETE CASCADE)");
        Log.d("Database", "Database created successfully!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOODS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEALS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GENERAL);
        onCreate(db);
    }

    public long addMeal(Meal meal, String date, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_MEAL_NAME, meal.getName());
        values.put(COLUMN_CALORIES, meal.getCalories());
        values.put(COLUMN_PROTEINS, meal.getProteins());
        values.put(COLUMN_FATS, meal.getFats());
        values.put(COLUMN_CARBS, meal.getCarbs());
        values.put(COLUMN_TIME, meal.getTime());
        long id = db.insert(TABLE_MEALS, null, values);
        updateGeneralForMeals(getDateForMeal((int) id), getUsernameForMeal((int) id));
        db.close();
        return id;
    }

    public void deleteMeal(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MEALS, COLUMN_MEAL_ID + "=?", new String[]{String.valueOf(id)});
        updateGeneralForMeals(getDateForMeal(id), getUsernameForMeal(id));
        db.close();
    }

    private String getDateForMeal(int mealId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT date FROM meals WHERE id = ?",
                new String[]{String.valueOf(mealId)}
        );

        String date = null;
        if (cursor.moveToFirst()) {
            date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
        }
        cursor.close();
        return date;
    }

    private String getUsernameForMeal(int mealId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT username FROM meals WHERE id = ?",
                new String[]{String.valueOf(mealId)}
        );

        String username = null;
        if (cursor.moveToFirst()) {
            username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
        }
        cursor.close();
        return username;
    }

    public int getmealid(String mealName, String date, String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT id FROM meals WHERE name = ? AND date = ? AND username = ?",
                new String[]{mealName, date, username}
        );

        int mealId = -1;
        if (cursor.moveToFirst()) {
            mealId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        }
        cursor.close();
        return mealId;
    }

    public void updatemeal(int mealId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(calories) AS totalCalories, " +
                        "SUM(proteins) AS totalProteins, " +
                        "SUM(fats) AS totalFats, " +
                        "SUM(carbs) AS totalCarbs " +
                        "FROM foods WHERE meal_id = ?", new String[]{String.valueOf(mealId)}
        );

        if (cursor.moveToFirst()) {
            ContentValues values = new ContentValues();
            values.put("calories", cursor.getDouble(cursor.getColumnIndexOrThrow("totalCalories")));
            values.put("proteins", cursor.getDouble(cursor.getColumnIndexOrThrow("totalProteins")));
            values.put("fats", cursor.getDouble(cursor.getColumnIndexOrThrow("totalFats")));
            values.put("carbs", cursor.getDouble(cursor.getColumnIndexOrThrow("totalCarbs")));

            db = this.getWritableDatabase();
            db.update("meals", values, "id = ?", new String[]{String.valueOf(mealId)});
        }
        cursor.close();
    }

    public void editMeal(int mealId, Meal newMeal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("name", newMeal.getName());
        values.put("date", newMeal.getTime());
        values.put("username", getUsernameForMeal(mealId));
        values.put("total_calories", newMeal.getCalories());
        values.put("total_proteins", newMeal.getProteins());
        values.put("total_fats", newMeal.getFats());
        values.put("total_carbs", newMeal.getCarbs());

        int rowsUpdated = db.update("meals", values, "id = ?", new String[]{String.valueOf(mealId)});
        db.close();
    }




    public void updateGeneralForMeals(String date, String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(calories) AS totalCalories, " +
                        "SUM(proteins) AS totalProteins, " +
                        "SUM(fats) AS totalFats, " +
                        "SUM(carbs) AS totalCarbs " +
                        "FROM meals WHERE date = ? AND username = ?",
                new String[]{date, username}
        );

        if (cursor.moveToFirst()) {
            ContentValues values = new ContentValues();
            values.put("total_calories", cursor.getDouble(cursor.getColumnIndexOrThrow("totalCalories")));
            values.put("total_proteins", cursor.getDouble(cursor.getColumnIndexOrThrow("totalProteins")));
            values.put("total_fats", cursor.getDouble(cursor.getColumnIndexOrThrow("totalFats")));
            values.put("total_carbs", cursor.getDouble(cursor.getColumnIndexOrThrow("totalCarbs")));

            db = this.getWritableDatabase();
            db.update("general", values, "username = ? AND date = ?", new String[]{username, date});
        }
        cursor.close();
    }

    public void updateGeneralForWater(String date, String username, int glassesWater) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("glasses_water", glassesWater);
        db.update("general", values, "username = ? AND date = ?", new String[]{username, date});
    }

    public int getGlassesWater(String username, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        int glassesWater = 0;

        String query = "SELECT " + COLUMN_GLASSES_WATER +
                " FROM " + TABLE_GENERAL +
                " WHERE " + COLUMN_USERNAME + " = ? AND " + COLUMN_DATE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username, date});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                glassesWater = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_GLASSES_WATER));
            }
            cursor.close();
        }

        db.close();
        return glassesWater;
    }

    public long addFood(long mealId, String foodName, float calories, float proteins, float fats, float carbs) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MEAL_ID, mealId);
        values.put(COLUMN_FOOD_NAME, foodName);
        values.put(COLUMN_CALORIES, calories);
        values.put(COLUMN_PROTEINS, proteins);
        values.put(COLUMN_FATS, fats);
        values.put(COLUMN_CARBS, carbs);
        return db.insert(TABLE_FOODS, null, values);
    }

    public void deletefood(int foodId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT meal_id FROM foods WHERE id = ?",
                new String[]{String.valueOf(foodId)}
        );

        int mealId = -1;
        if (cursor.moveToFirst()) {
            mealId = cursor.getInt(cursor.getColumnIndexOrThrow("meal_id"));
        }
        cursor.close();

        db = this.getWritableDatabase();
        db.delete("foods", "id = ?", new String[]{String.valueOf(foodId)});

        if (mealId != -1) {
            updatemeal(mealId);
            updateGeneralForMeals(getDateForMeal(mealId), getUsernameForMeal(mealId));
        }
    }

    public void dumpDatabaseContents() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor generalCursor = db.rawQuery("SELECT * FROM " + TABLE_GENERAL, null);
        if (generalCursor != null && generalCursor.moveToFirst()) {
            do {
                String username = generalCursor.getString(generalCursor.getColumnIndex(COLUMN_USERNAME));
                String date = generalCursor.getString(generalCursor.getColumnIndex(COLUMN_DATE));
                double totalCalories = generalCursor.getDouble(generalCursor.getColumnIndex(COLUMN_TOTAL_CALORIES));
                double totalProteins = generalCursor.getDouble(generalCursor.getColumnIndex(COLUMN_TOTAL_PROTEINS));
                double totalFats = generalCursor.getDouble(generalCursor.getColumnIndex(COLUMN_TOTAL_FATS));
                double totalCarbs = generalCursor.getDouble(generalCursor.getColumnIndex(COLUMN_TOTAL_CARBS));
                int glassesWater = generalCursor.getInt(generalCursor.getColumnIndex(COLUMN_GLASSES_WATER));

                Log.d("DatabaseDump", "General - Username: " + username + ", Date: " + date +
                        ", Calories: " + totalCalories + ", Proteins: " + totalProteins +
                        ", Fats: " + totalFats + ", Carbs: " + totalCarbs +
                        ", Glasses of Water: " + glassesWater);
            } while (generalCursor.moveToNext());
        }
        generalCursor.close();

        Cursor mealsCursor = db.rawQuery("SELECT * FROM " + TABLE_MEALS, null);
        if (mealsCursor != null && mealsCursor.moveToFirst()) {
            do {
                int mealId = mealsCursor.getInt(mealsCursor.getColumnIndex(COLUMN_MEAL_ID));
                String username = mealsCursor.getString(mealsCursor.getColumnIndex(COLUMN_USERNAME));
                String date = mealsCursor.getString(mealsCursor.getColumnIndex(COLUMN_DATE));
                String mealTime = mealsCursor.getString(mealsCursor.getColumnIndex(COLUMN_TIME));
                String mealName = mealsCursor.getString(mealsCursor.getColumnIndex(COLUMN_MEAL_NAME));
                double mealCalories = mealsCursor.getDouble(mealsCursor.getColumnIndex(COLUMN_TOTAL_CALORIES));
                double mealProteins = mealsCursor.getDouble(mealsCursor.getColumnIndex(COLUMN_TOTAL_PROTEINS));
                double mealFats = mealsCursor.getDouble(mealsCursor.getColumnIndex(COLUMN_TOTAL_FATS));
                double mealCarbs = mealsCursor.getDouble(mealsCursor.getColumnIndex(COLUMN_TOTAL_CARBS));

                Log.d("DatabaseDump", "Meal - ID: " + mealId + ", Username: " + username +
                        ", Date: " + date + ", Time: " + mealTime + ", Meal Name: " + mealName +
                        ", Calories: " + mealCalories + ", Proteins: " + mealProteins +
                        ", Fats: " + mealFats + ", Carbs: " + mealCarbs);
            } while (mealsCursor.moveToNext());
        }
        mealsCursor.close();

        Cursor foodsCursor = db.rawQuery("SELECT * FROM " + TABLE_FOODS, null);
        if (foodsCursor != null && foodsCursor.moveToFirst()) {
            do {
                int foodId = foodsCursor.getInt(foodsCursor.getColumnIndex(COLUMN_FOOD_ID));
                int mealId = foodsCursor.getInt(foodsCursor.getColumnIndex(COLUMN_MEAL_ID));
                String foodName = foodsCursor.getString(foodsCursor.getColumnIndex(COLUMN_FOOD_NAME));
                double foodCalories = foodsCursor.getDouble(foodsCursor.getColumnIndex(COLUMN_CALORIES));
                double foodProteins = foodsCursor.getDouble(foodsCursor.getColumnIndex(COLUMN_PROTEINS));
                double foodFats = foodsCursor.getDouble(foodsCursor.getColumnIndex(COLUMN_FATS));
                double foodCarbs = foodsCursor.getDouble(foodsCursor.getColumnIndex(COLUMN_CARBS));

                Log.d("DatabaseDump", "Food - ID: " + foodId + ", Meal ID: " + mealId +
                        ", Food Name: " + foodName + ", Calories: " + foodCalories +
                        ", Proteins: " + foodProteins + ", Fats: " + foodFats +
                        ", Carbs: " + foodCarbs);
            } while (foodsCursor.moveToNext());
        }
        foodsCursor.close();
    }


}

