package com.example.helpmefind;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
    ArrayList<Resource> resourceArrayList;

    //Resource libraryMicrowave = new Resource("College Library 1F Microwave", "microwave",43.076656, -89.401360,"600 N Park St, Madison, WI 53706");
    //Resource libraryRestroom = new Resource("1197 College Library: Restroom + Changing Room", "restroom", 43.076656, -89.401360,"600 N Park St, Madison, WI 53706");
   //Resource memUnionRestroom = new Resource("Memorial Union Lower Level: Restroom + Wheelchair Accessible", "restroom", 43.075840, -89.399830,"800 Langdon St, Madison, WI 53706");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        //resourceArrayList.add(libraryMicrowave);
        //resourceArrayList.add(libraryRestroom);
        //resourceArrayList.add(memUnionRestroom);

        Intent i = this.getIntent();
        resourceArrayList =  i.getParcelableArrayListExtra("resources");
        bottomNavigationView = findViewById(R.id.bottomnav);
        bottomNavigationView.setOnItemSelectedListener(bottomnavFunction);
    }

    protected ArrayList<Resource> getResourceArrayList(){
        return resourceArrayList;
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