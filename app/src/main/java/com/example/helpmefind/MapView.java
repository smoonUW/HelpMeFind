package com.example.helpmefind;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationBarView;

import java.io.Serializable;
import java.util.ArrayList;

public class MapView extends AppCompatActivity {

    NavigationBarView bottomNavigationView;
    private MapView thisObject = this;
    ArrayList<Resource> resourceArrayList;
    Resource selectedResource = null;
    double selectedRadius;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("MAP VIEW","ARRIVED");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        bottomNavigationView = findViewById(R.id.bottomnav);
        bottomNavigationView.setOnItemSelectedListener(bottomnavFunction);

        MenuItem item =  bottomNavigationView.getMenu().findItem(R.id.select);
        item.setVisible(false);
        item.setEnabled(false);

        Intent intent = getIntent();
        resourceArrayList = (ArrayList<Resource>) intent.getSerializableExtra("list");
        selectedRadius = (Double) intent.getDoubleExtra("radius", 0.5);

        for (Resource r : resourceArrayList){
            Log.i("R2String", r.toString());
        }
    }

    protected ArrayList<Resource> getResourceArrayList(){
        return resourceArrayList;
    }
    protected void setSelectedResource(Resource r){
        MenuItem item = bottomNavigationView.getMenu().findItem(R.id.select);
        if (r == null){
            item.setVisible(false);
            item.setEnabled(false);
        } else {
            item.setVisible(true);
            item.setEnabled(true);
        }
        selectedResource = r;
    }
    protected Double getSelectedRadius(){
        return selectedRadius;
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
                Intent intent = new Intent(thisObject, Wayfinder.class);
                if (selectedResource != null){
                    intent.putExtra("selectedResource", (Serializable) selectedResource);
                }
                startActivity(intent);
                return true;
            }
            return true;
        }
    };
}