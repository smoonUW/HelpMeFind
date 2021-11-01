package com.example.helpmefind;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

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

    public void saveSelection() {
        CheckBox microwave = (CheckBox) findViewById(R.id.microwaveCheckBox);
        CheckBox bathroom = (CheckBox) findViewById(R.id.bathroomCheckBox);
        CheckBox fridge = (CheckBox) findViewById(R.id.refrigeratorCheckBox);
        CheckBox lactation = (CheckBox) findViewById(R.id.lactationCheckBox);
        // TODO check whether each box is checked, save state for use in next activities
        // TODO use shared preferences?
    }

    public void goToARView(View view) {
        saveSelection();
        // TODO get destination activity name
//        Intent intent = new Intent();
//        startActivity(intent);
    }

    public void goToMapView(View view) {
        saveSelection();
        // TODO get destination activity name
//        Intent intent = new Intent();
//        startActivity(intent);
    }
}