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

    public boolean saveSelection() {
        CheckBox microwave = (CheckBox) findViewById(R.id.microwaveCheckBox);
        CheckBox bathroom = (CheckBox) findViewById(R.id.bathroomCheckBox);
        CheckBox fridge = (CheckBox) findViewById(R.id.refrigeratorCheckBox);
        CheckBox lactation = (CheckBox) findViewById(R.id.lactationCheckBox);

        if (!microwave.isChecked() && !bathroom.isChecked() && !fridge.isChecked() && !lactation.isChecked()) {
            return false;
        }

        SharedPreferences sharedPreferences = getSharedPreferences("com.example.helpmefind", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("microwave", String.valueOf(microwave.isChecked())).apply();
        sharedPreferences.edit().putString("bathroom", String.valueOf(bathroom.isChecked())).apply();
        sharedPreferences.edit().putString("refrigerator", String.valueOf(fridge.isChecked())).apply();
        sharedPreferences.edit().putString("lactation", String.valueOf(lactation.isChecked())).apply();
        sharedPreferences.edit().putString("radius", String.valueOf(selectedRadius)).apply();

        return true;
    }

    public void goToARView(View view) {
        boolean selectionMade = saveSelection();

        if (selectedRadius == -1 || !selectionMade) {
            Toast.makeText(this, "Please select at least one resource and a radius value", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(this, ARVerbose.class);
            startActivity(intent);
        }
    }

    public void goToMapView(View view) {
        boolean selectionMade = saveSelection();

        if (selectedRadius == -1 || !selectionMade) {
            Toast.makeText(this, "Please select at least one resource and a radius value", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(this, MapView.class);
            ArrayList<Resource> resourceList = new ArrayList();
            resourceList.add(new Resource("College Library 1F Microwave FILTER SEARCH", "microwave",43.076656, -89.401360,"600 N Park St, Madison, WI 53706"));
            resourceList.add(new Resource("1197 College Library: Restroom + Changing Room", "restroom", 43.076656, -89.401360,"600 N Park St, Madison, WI 53706"));
            resourceList.add(new Resource("Memorial Union Lower Level: Restroom + Wheelchair Accessible", "restroom", 43.075840, -89.399830,"800 Langdon St, Madison, WI 53706"));
            intent.putParcelableArrayListExtra("resources", resourceList);
            this.startActivity(intent);
            startActivity(intent);
        }
    }
}