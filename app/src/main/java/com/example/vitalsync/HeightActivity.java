package com.example.vitalsync;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class HeightActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "HeightPrefs";
    private static final String HEIGHT_METERS_KEY = "HeightMeters";
    private static final String HEIGHT_CENTIMETERS_KEY = "HeightCentimeters";
    private static final String UNIT_SYSTEM_KEY = "UnitSystem";

    private NumberPicker meterPicker, cmPicker;
    private ImageButton nextButton;
    private TextView backText;
    private Switch unitSwitch;
    private int meters = 0, centimeters = 0;
    private boolean isMetric = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_height);

        meterPicker = findViewById(R.id.meter_picker);
        cmPicker = findViewById(R.id.cm_picker);
        nextButton = findViewById(R.id.proceed_button);
        backText = findViewById(R.id.textView);
        unitSwitch = findViewById(R.id.unit_switch);

        loadPreferences();

        unitSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isMetric = !isChecked;
            unitSwitch.setText(isMetric ? "Switch to Imperial" : "Switch to Metric");
            if (isMetric) {
                setupMetricPickers();
            } else {
                setupImperialPickers();
            }
            savePreferences();
        });

        meterPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            meters = newVal;
            savePreferences();
        });

        cmPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            centimeters = newVal;
            savePreferences();
        });

        backText.setOnClickListener(v -> finish());

        nextButton.setOnClickListener(v -> {
            if (meters == 0 && centimeters == 0) {
                Toast.makeText(HeightActivity.this, "Height cannot be zero", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(HeightActivity.this, WeightActivity.class);
                intent.putExtra("heightMeters", meters);
                intent.putExtra("heightCentimeters", centimeters);
                intent.putExtra("isMetric", isMetric);
                startActivity(intent);
            }
        });
    }

    private void setupMetricPickers() {
        meterPicker.setMinValue(0);
        meterPicker.setMaxValue(3);
        cmPicker.setMinValue(0);
        cmPicker.setMaxValue(99);

        TextView meterLabel = findViewById(R.id.meter_label);
        TextView cmLabel = findViewById(R.id.cm_label);
        meterLabel.setText("m");
        cmLabel.setText("cm");

        meterPicker.setValue(meters);
        cmPicker.setValue(centimeters);
    }

    private void setupImperialPickers() {
        meterPicker.setMinValue(0);
        meterPicker.setMaxValue(8);
        cmPicker.setMinValue(0);
        cmPicker.setMaxValue(11);

        TextView meterLabel = findViewById(R.id.meter_label);
        TextView cmLabel = findViewById(R.id.cm_label);
        meterLabel.setText("ft");
        cmLabel.setText("in");

        meterPicker.setValue(meters);
        cmPicker.setValue(centimeters);
    }

    private void savePreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(HEIGHT_METERS_KEY, meters);
        editor.putInt(HEIGHT_CENTIMETERS_KEY, centimeters);
        editor.putBoolean(UNIT_SYSTEM_KEY, isMetric);
        editor.apply();
    }

    private void loadPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        meters = sharedPreferences.getInt(HEIGHT_METERS_KEY, 0);
        centimeters = sharedPreferences.getInt(HEIGHT_CENTIMETERS_KEY, 0);
        isMetric = sharedPreferences.getBoolean(UNIT_SYSTEM_KEY, true);

        unitSwitch.setChecked(!isMetric);
        unitSwitch.setText(isMetric ? "Switch to Imperial" : "Switch to Metric");

        if (isMetric) {
            setupMetricPickers();
        } else {
            setupImperialPickers();
        }
    }
}
