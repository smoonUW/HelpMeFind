package com.example.helpmefind;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.logging.Filter;

public class MapView extends AppCompatActivity {

    NavigationBarView bottomNavigationView;
    private MapView thisObject = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("MAP VIEW","ARRIVED");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        bottomNavigationView = findViewById(R.id.bottomnav);
        bottomNavigationView.setOnItemSelectedListener(bottomnavFunction);

        Intent intent = getIntent();

        ArrayList<Resource> resources = (ArrayList<Resource>) intent.getSerializableExtra("list");
        for (Resource r : resources) {
            Log.i("MAPVIEW",r.toString());
        }
    }

    private NavigationBarView.OnItemSelectedListener bottomnavFunction = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            if (item.getItemId() == R.id.back) {
                Log.i("MapView", "back pressed.");
                Intent intent = new Intent(thisObject, FilterSearch.class);
                startActivity(intent);
                return true;
            }
            else if (item.getItemId() == R.id.select) {
                // do something with the selection
                Log.i("MapView", "select pressed.");
                Intent intent = new Intent(thisObject, ARSelected.class);
                startActivity(intent);
                return true;
            }
            return true;
        }
    };
}