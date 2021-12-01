package com.example.helpmefind;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Feedback extends AppCompatActivity {

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "FEEDBACK";

    public void submitFeedback(View view) {
        CheckBox cb = findViewById((R.id.resourceIssueCheckBox));
        boolean checked = cb.isChecked();

        EditText problemEditText = findViewById(R.id.explainProblemEditText);
        String problem = problemEditText.getText().toString();

        EditText feedbackEditText = findViewById(R.id.editFeedback);
        String feedback = feedbackEditText.getText().toString();

        CollectionReference res = db.collection("comment suggestions");
        Map<String, Object> feedbackDocument = new HashMap<>();
        feedbackDocument.put("hasProblem", checked);
        feedbackDocument.put("problem", problem);
        feedbackDocument.put("feedback", feedback);

        db.collection("comment suggestions").document(" ID ?? ")
                .set(feedbackDocument)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

        changeToSearchActivity();
    }

    public void goToSearch(View view) {
        changeToSearchActivity();
    }

    public void changeToSearchActivity() {
        Intent intent = new Intent(this, FilterSearch.class);
        startActivity(intent);
    }

    public void onCheckboxClicked(View view) {
        if (((CheckBox) view).isChecked()) {
            ((CheckBox) view).setText("Yes");
        } else {
            ((CheckBox) view).setText("No");
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        // TODO : get ID of resource
    }
}