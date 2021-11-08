package com.example.helpmefind;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;

public class Welcome extends AppCompatActivity {

    // activity does not show, issue may be with thread.sleep,
    // either main thread refuses to sleep or another method
    // is needed (onPostDelayed?) instead of thread.sleep. in
    // either case the progress bar is just for
    // show at the moment
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        ProgressBar progressBar=(ProgressBar) findViewById(R.id.progressBar);
        for (int i = 1; i <= 100; i++) {
            progressBar.setProgress(i);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Intent intent = new Intent(this, FilterSearch.class);
        startActivity(intent);
    }
}