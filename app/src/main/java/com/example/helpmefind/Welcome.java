package com.example.helpmefind;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

public class Welcome extends AppCompatActivity implements View.OnClickListener {

    // activity does not show, issue may be with thread.sleep,
    // either main thread refuses to sleep or another method
    // is needed (onPostDelayed?) instead of thread.sleep. in
    // either case the progress bar is just for
    // show at the moment
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);
        this.findViewById(R.id.welcomeTitle).setOnClickListener(this::onClick);
        this.findViewById(R.id.welcomeSubheader).setOnClickListener(this::onClick);
        this.findViewById(R.id.imageView2).setOnClickListener(this::onClick);
//        ProgressBar progressBar=(ProgressBar) findViewById(R.id.progressBar);
//        for (int i = 1; i <= 100; i++) {
//            progressBar.setProgress(i);
//            try {
//                Thread.sleep(50);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
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