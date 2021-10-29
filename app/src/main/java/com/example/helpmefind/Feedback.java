package com.example.helpmefind;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class Feedback extends AppCompatActivity {

    public void submitFeedback(View view) {
        EditText editText = findViewById(R.id.editFeedback);
        String feedback = editText.getText().toString();
        // Do something with the string to store it
        changeToSearchActivity();
    }

    public void goToSearch(View view) {
        changeToSearchActivity();
    }

    public void changeToSearchActivity() {
        //Intent intent = new Intent(this, <activityname>);
        //startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
    }
}