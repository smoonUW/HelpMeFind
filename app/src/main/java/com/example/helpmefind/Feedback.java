package com.example.helpmefind;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Feedback extends AppCompatActivity {

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "FEEDBACK";
    // TODO : temp hardcoded resource id for mem u, get id from view activities
    private static String resourceID = "MXhRvoG0npPSzeELriNI";

    public void submitFeedback(View view) {
        CheckBox cb = findViewById((R.id.resourceIssueCheckBox));
        boolean checked = cb.isChecked();

        EditText problemEditText = findViewById(R.id.explainProblemEditText);
        String problem = problemEditText.getText().toString();

        EditText commentEditText = findViewById(R.id.commentEditText);
        String comment = commentEditText.getText().toString();

        CollectionReference res = db.collection("comment suggestions");
        Map<String, Object> commentDocument = new HashMap<>();
        commentDocument.put("comment", comment);
        commentDocument.put("r_id", resourceID);
        commentDocument.put("time", new Timestamp(new Date()));

        db.collection("comment suggestions")
                .add(commentDocument)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Successfully added comment document!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding comment document", e);
                    }
                });

        if (checked) {
            if (problem.length()==0) {
                Toast.makeText(this, "Please explain the issue with resource.", Toast.LENGTH_LONG).show();
            } else {
                Map<String, Object> problemDocument = new HashMap<>();
                problemDocument.put("issue", problem);
                problemDocument.put("r_id", resourceID);
                problemDocument.put("time", new Timestamp(new Date()));

                db.collection("resource issues")
                        .add(problemDocument)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "Successfully added problem document!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding problem document", e);
                            }
                        });
            }
        }

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