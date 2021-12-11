package com.example.helpmefind;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationBarView;

import java.io.Serializable;

public class ARSelected extends AppCompatActivity {

    NavigationBarView bottomNavigationView;
    Resource selectedResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arselected);
        Intent intent = getIntent();
        selectedResource = (Resource) intent.getSerializableExtra("selectedResource");

        bottomNavigationView = findViewById(R.id.bottomnav);
        bottomNavigationView.setOnItemSelectedListener(bottomnavFunction);
    }
    private NavigationBarView.OnItemSelectedListener bottomnavFunction = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if (item.getItemId() == R.id.back) {
                Intent intent = new Intent(ARSelected.this, FilterSearch.class);
                startActivity(intent);
                return true;
            }
            else if (item.getItemId() == R.id.arrived) {
                Intent intent = new Intent(ARSelected.this, Feedback.class);
                intent.putExtra("selectedResource", (Serializable) selectedResource);
                startActivity(intent);
                return true;
            }
            return true;
        }
    };
}