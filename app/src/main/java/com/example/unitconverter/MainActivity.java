package com.example.unitconverter;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerCategory;
    private Spinner spinnerSourceUnit;
    private Spinner spinnerTargetUnit;
    private EditText editTextInputValue;
    private Button buttonConvert;
    private Button buttonSwapUnits;
    private TextView textViewResult;
    private TextView textViewFormulaDetail;

    private static final String[] CATEGORIES = {
            "Length",
            "Weight",
            "Volume",
            "Temperature"
    };

    private static final String[] LENGTH_UNITS = {
            "Centimetres (cm)",
            "Metres (m)",
            "Kilometres (km)",
            "Millimetres (mm)",
            "Inches (in)",
            "Feet (ft)",
            "Yards (yd)",
            "Miles (mi)"
    };

    private static final String[] WEIGHT_UNITS = {
            "Grams (g)",
            "Kilograms (kg)",
            "Milligrams (mg)",
            "Metric Tonnes (t)",
            "Ounces (oz)",
            "Pounds (lb)"
    };

    private static final String[] VOLUME_UNITS = {
            "Litres (L)",
            "Millilitres (mL)",
            "Cubic Metres (m³)",
            "US Fluid Ounces (fl oz)",
            "US Cups (cup)",
            "US Gallons (gal)"
    };

    private static final String[] TEMPERATURE_UNITS = {
            "Celsius (°C)",
            "Fahrenheit (°F)",
            "Kelvin (K)"
    };

    private final DecimalFormat resultFormatter = new DecimalFormat("#,##0.######");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerSourceUnit = findViewById(R.id.spinnerSourceUnit);
        spinnerTargetUnit = findViewById(R.id.spinnerTargetUnit);
        editTextInputValue = findViewById(R.id.editTextInputValue);
        buttonConvert = findViewById(R.id.buttonConvert);
        buttonSwapUnits = findViewById(R.id.buttonSwapUnits);
        textViewResult = findViewById(R.id.textViewResult);
        textViewFormulaDetail = findViewById(R.id.textViewFormulaDetail);

        setupCategorySpinner();
        setupButtonListeners();
    }

    private void setupCategorySpinner() {
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                CATEGORIES
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateUnitSpinnersForCategory(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                updateUnitSpinnersForCategory(0);
            }
        });
    }

    private void updateUnitSpinnersForCategory(int categoryIndex) {
        String[] unitsForSelectedCategory;
        switch (categoryIndex) {
            case 0:
                unitsForSelectedCategory = LENGTH_UNITS;
                break;
            case 1:
                unitsForSelectedCategory = WEIGHT_UNITS;
                break;
            case 2:
                unitsForSelectedCategory = VOLUME_UNITS;
                break;
            case 3:
                unitsForSelectedCategory = TEMPERATURE_UNITS;
                break;
            default:
                unitsForSelectedCategory = LENGTH_UNITS;
                break;
        }

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                unitsForSelectedCategory
        );
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerSourceUnit.setAdapter(unitAdapter);
        spinnerTargetUnit.setAdapter(unitAdapter);

        spinnerSourceUnit.setSelection(0);
        if (unitsForSelectedCategory.length > 1) {
            spinnerTargetUnit.setSelection(1);
        }

        textViewResult.setText(getString(R.string.default_result_placeholder));
        textViewFormulaDetail.setText(getString(R.string.default_formula_placeholder));
    }

    private void setupButtonListeners() {
        buttonConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performConversion();
            }
        });

        buttonSwapUnits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sourcePos = spinnerSourceUnit.getSelectedItemPosition();
                int targetPos = spinnerTargetUnit.getSelectedItemPosition();
                spinnerSourceUnit.setSelection(targetPos);
                spinnerTargetUnit.setSelection(sourcePos);

                String rawInput = editTextInputValue.getText().toString().trim();
                if (!rawInput.isEmpty()) {
                    performConversion();
                }
            }
        });
    }

    private void performConversion() {
        String rawInput = editTextInputValue.getText().toString().trim();

        if (rawInput.isEmpty()) {
            Toast.makeText(
                    MainActivity.this,
                    "Please enter a numeric value to convert.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        double inputValue;
        try {
            inputValue = Double.parseDouble(rawInput);
            if (Double.isNaN(inputValue) || Double.isInfinite(inputValue)) {
                throw new NumberFormatException("NaN or Infinite value");
            }
        } catch (NumberFormatException e) {
            Toast.makeText(
                    MainActivity.this,
                    "Invalid input! Please enter a valid numeric value.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String category = spinnerCategory.getSelectedItem().toString();
        String sourceUnit = spinnerSourceUnit.getSelectedItem().toString();
        String targetUnit = spinnerTargetUnit.getSelectedItem().toString();

        double convertedValue;
        switch (category) {
            case "Length":
                convertedValue = convertLength(inputValue, sourceUnit, targetUnit);
                break;
            case "Weight":
                convertedValue = convertWeight(inputValue, sourceUnit, targetUnit);
                break;
            case "Volume":
                convertedValue = convertVolume(inputValue, sourceUnit, targetUnit);
                break;
            case "Temperature":
                convertedValue = convertTemperature(inputValue, sourceUnit, targetUnit);
                break;
            default:
                convertedValue = inputValue;
                break;
        }

        String sourceShortLabel = extractUnitSymbol(sourceUnit);
        String targetShortLabel = extractUnitSymbol(targetUnit);

        String formattedInput = resultFormatter.format(inputValue);
        String formattedOutput = resultFormatter.format(convertedValue);

        String resultDisplay = String.format(
                Locale.US,
                "%s %s",
                formattedOutput,
                targetShortLabel
        );
        textViewResult.setText(resultDisplay);

        String detailDisplay = String.format(
                Locale.US,
                "%s %s = %s %s",
                formattedInput,
                sourceShortLabel,
                formattedOutput,
                targetShortLabel
        );
        textViewFormulaDetail.setText(detailDisplay);
    }

    private double convertLength(double value, String fromUnit, String toUnit) {
        double valueInMetres;
        switch (fromUnit) {
            case "Centimetres (cm)":
                valueInMetres = value * 0.01;
                break;
            case "Metres (m)":
                valueInMetres = value;
                break;
            case "Kilometres (km)":
                valueInMetres = value * 1000.0;
                break;
            case "Millimetres (mm)":
                valueInMetres = value * 0.001;
                break;
            case "Inches (in)":
                valueInMetres = value * 0.0254;
                break;
            case "Feet (ft)":
                valueInMetres = value * 0.3048;
                break;
            case "Yards (yd)":
                valueInMetres = value * 0.9144;
                break;
            case "Miles (mi)":
                valueInMetres = value * 1609.344;
                break;
            default:
                valueInMetres = value;
                break;
        }

        switch (toUnit) {
            case "Centimetres (cm)":
                return valueInMetres / 0.01;
            case "Metres (m)":
                return valueInMetres;
            case "Kilometres (km)":
                return valueInMetres / 1000.0;
            case "Millimetres (mm)":
                return valueInMetres / 0.001;
            case "Inches (in)":
                return valueInMetres / 0.0254;
            case "Feet (ft)":
                return valueInMetres / 0.3048;
            case "Yards (yd)":
                return valueInMetres / 0.9144;
            case "Miles (mi)":
                return valueInMetres / 1609.344;
            default:
                return valueInMetres;
        }
    }

    private double convertWeight(double value, String fromUnit, String toUnit) {
        double valueInGrams;
        switch (fromUnit) {
            case "Grams (g)":
                valueInGrams = value;
                break;
            case "Kilograms (kg)":
                valueInGrams = value * 1000.0;
                break;
            case "Milligrams (mg)":
                valueInGrams = value * 0.001;
                break;
            case "Metric Tonnes (t)":
                valueInGrams = value * 1_000_000.0;
                break;
            case "Ounces (oz)":
                valueInGrams = value * 28.349523125;
                break;
            case "Pounds (lb)":
                valueInGrams = value * 453.59237;
                break;
            default:
                valueInGrams = value;
                break;
        }

        switch (toUnit) {
            case "Grams (g)":
                return valueInGrams;
            case "Kilograms (kg)":
                return valueInGrams / 1000.0;
            case "Milligrams (mg)":
                return valueInGrams / 0.001;
            case "Metric Tonnes (t)":
                return valueInGrams / 1_000_000.0;
            case "Ounces (oz)":
                return valueInGrams / 28.349523125;
            case "Pounds (lb)":
                return valueInGrams / 453.59237;
            default:
                return valueInGrams;
        }
    }

    private double convertVolume(double value, String fromUnit, String toUnit) {
        double valueInLitres;
        switch (fromUnit) {
            case "Litres (L)":
                valueInLitres = value;
                break;
            case "Millilitres (mL)":
                valueInLitres = value * 0.001;
                break;
            case "Cubic Metres (m³)":
                valueInLitres = value * 1000.0;
                break;
            case "US Fluid Ounces (fl oz)":
                valueInLitres = value * 0.0295735295625;
                break;
            case "US Cups (cup)":
                valueInLitres = value * 0.2365882365;
                break;
            case "US Gallons (gal)":
                valueInLitres = value * 3.785411784;
                break;
            default:
                valueInLitres = value;
                break;
        }

        switch (toUnit) {
            case "Litres (L)":
                return valueInLitres;
            case "Millilitres (mL)":
                return valueInLitres / 0.001;
            case "Cubic Metres (m³)":
                return valueInLitres / 1000.0;
            case "US Fluid Ounces (fl oz)":
                return valueInLitres / 0.0295735295625;
            case "US Cups (cup)":
                return valueInLitres / 0.2365882365;
            case "US Gallons (gal)":
                return valueInLitres / 3.785411784;
            default:
                return valueInLitres;
        }
    }

    private double convertTemperature(double value, String fromUnit, String toUnit) {
        double celsius;
        switch (fromUnit) {
            case "Celsius (°C)":
                celsius = value;
                break;
            case "Fahrenheit (°F)":
                celsius = (value - 32.0) * 5.0 / 9.0;
                break;
            case "Kelvin (K)":
                celsius = value - 273.15;
                break;
            default:
                celsius = value;
                break;
        }

        switch (toUnit) {
            case "Celsius (°C)":
                return celsius;
            case "Fahrenheit (°F)":
                return (celsius * 9.0 / 5.0) + 32.0;
            case "Kelvin (K)":
                return celsius + 273.15;
            default:
                return celsius;
        }
    }

    private String extractUnitSymbol(String fullUnitLabel) {
        int openParen = fullUnitLabel.indexOf('(');
        int closeParen = fullUnitLabel.indexOf(')');
        if (openParen != -1 && closeParen != -1 && closeParen > openParen) {
            return fullUnitLabel.substring(openParen + 1, closeParen);
        }
        return fullUnitLabel;
    }
}