package com.example.helpmefind;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

public class Welcome extends AppCompatActivity implements View.OnClickListener {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        this.findViewById(R.id.welcomeTitle).setOnClickListener(this::onClick);
        this.findViewById(R.id.welcomeSubheader).setOnClickListener(this::onClick);
        this.findViewById(R.id.imageView2).setOnClickListener(this::onClick);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, FilterSearch.class);
        startActivity(intent);
    }
}