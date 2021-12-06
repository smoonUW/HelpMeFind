package com.example.helpmefind;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.logging.Filter;

public class MapView extends AppCompatActivity {

    NavigationBarView bottomNavigationView;
    private MapView thisObject = this;
    ArrayList<Resource> resourceArrayList;
    Resource selectedResource = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("MAP VIEW","ARRIVED");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        bottomNavigationView = findViewById(R.id.bottomnav);
        bottomNavigationView.setOnItemSelectedListener(bottomnavFunction);
        bottomNavigationView.getMenu().findItem(R.id.select).setEnabled(false);
        Intent intent = getIntent();

        resourceArrayList = (ArrayList<Resource>) intent.getSerializableExtra("list");
        for (Resource r : resourceArrayList){
            Log.i("R2String", r.toString());
        }
    }

    protected ArrayList<Resource> getResourceArrayList(){
        return resourceArrayList;
    }
    protected void setSelectedResource(Resource r){
        if (r == null){
            bottomNavigationView.getMenu().findItem(R.id.select).setEnabled(false);
        } else {
            bottomNavigationView.getMenu().findItem(R.id.select).setEnabled(true);
        }
        selectedResource = r;
    }
    protected Resource getSelectedResource() {
        return selectedResource;
    }
    protected void selectResource(Resource r){
        selectedResource = r;
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
                if (selectedResource != null){
                    intent.putExtra("selectedResource", selectedResource);
                }
                startActivity(intent);
                return true;
            }
            return true;
        }
    };
}