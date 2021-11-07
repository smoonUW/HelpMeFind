package com.example.helpmefind;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationBarView;

public class ARSelected extends AppCompatActivity {

    NavigationBarView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arselected);

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
            else if (item.getItemId() == R.id.select) {
                // do something with the selection
                Intent intent = new Intent(ARSelected.this, ARSelected.class);
                startActivity(intent);
                return true;
            }
            else if (item.getItemId() == R.id.arrived) {
                Intent intent = new Intent(ARSelected.this, Feedback.class);
                startActivity(intent);
                return true;
            }
            return true;
        }
    };
}