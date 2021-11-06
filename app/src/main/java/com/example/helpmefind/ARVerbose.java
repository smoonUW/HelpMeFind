package com.example.helpmefind;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationBarView;

public class ARVerbose extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arverbose);
    }

    private NavigationBarView.OnItemSelectedListener bottomnavFunction = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if (item.getItemId() == R.id.back) {
                Intent intent = new Intent(ARVerbose.this, FilterSearch.class);
                startActivity(intent);
                return true;
            }
            else if (item.getItemId() == R.id.select) {
                // do something with the selection
                Intent intent = new Intent(ARVerbose.this, ARSelected.class);
                startActivity(intent);
                return true;
            }
            else if (item.getItemId() == R.id.arrived) {
                Intent intent = new Intent(ARVerbose.this, Feedback.class);
                startActivity(intent);
                return true;
            }
            return true;
        }
    };
}