package com.example.helpmefind;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class FilterSearch extends AppCompatActivity {

    double selectedRadius = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_search);

        Spinner radiusMenu = findViewById(R.id.radiusDropdown);

        ArrayAdapter<CharSequence> adapter= ArrayAdapter.createFromResource(this, R.array.radii, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        radiusMenu.setAdapter(adapter);

        radiusMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                // dumbest line of code I've ever written
                selectedRadius = Double.parseDouble(parent.getItemAtPosition(position).toString().split(" ")[0]);

                Log.i("selected radius", String.valueOf(selectedRadius));
            }

            public void onNothingSelected(AdapterView<?> parent)
            {
                // Do nothing?
            }
        });
    }

    public ArrayList<String> saveSelection() {
        CheckBox microwave = (CheckBox) findViewById(R.id.microwaveCheckBox);
        CheckBox bathroom = (CheckBox) findViewById(R.id.bathroomCheckBox);
        CheckBox fridge = (CheckBox) findViewById(R.id.refrigeratorCheckBox);
        CheckBox lactation = (CheckBox) findViewById(R.id.lactationCheckBox);

        ArrayList<String> types = new ArrayList<String>();
        if (microwave.isChecked()) {
            types.add("microwave");
        }
        if (bathroom.isChecked()) {
            types.add("bathroom");
        }
        if (fridge.isChecked()) {
            types.add("fridge");
        }
        if (lactation.isChecked()) {
            types.add("lactation");
        }

        return types;
    }

    public void goToARView(View view) {
        ArrayList<String> types = saveSelection();

        if (selectedRadius == -1 || types.size() == 0) {
            Toast.makeText(this, "Please select at least one resource and a radius value", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(this, ARVerbose.class);
            intent.putStringArrayListExtra("types", types);
            startActivity(intent);
        }
    }

    public void goToMapView(View view) {
        ArrayList<String> types = saveSelection();

        if (selectedRadius == -1 || types.size() == 0) {
            Toast.makeText(this, "Please select at least one resource and a radius value", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(this, MapView.class);
            intent.putStringArrayListExtra("types", types);
            startActivity(intent);
        }
    }
}