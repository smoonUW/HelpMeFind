package com.example.helpmefind;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Feedback extends AppCompatActivity {

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "FEEDBACK";
    private static String resourceID;

    public void submitFeedback(View view) {
        if (resourceID==null){
            Toast.makeText(this, "Error collecting resource info. Try Again.", Toast.LENGTH_LONG).show();
            return;
        }

        CheckBox cb = findViewById((R.id.resourceIssueCheckBox));
        boolean checked = cb.isChecked();

        EditText problemEditText = findViewById(R.id.explainProblemEditText);
        String problem = problemEditText.getText().toString();

        EditText commentEditText = findViewById(R.id.commentEditText);
        String comment = commentEditText.getText().toString();

        if (comment.length()!=0) {
            if (!checked) {
                Toast.makeText(this, "Please fill in at least one field.", Toast.LENGTH_LONG).show();
                return;
            }
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
        }

        if (checked) {
            if (problem.length()==0) {
                Toast.makeText(this, "Please explain the issue with resource.", Toast.LENGTH_LONG).show();
                return;
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

        Intent intent = getIntent();
        Resource resource = (Resource) intent.getSerializableExtra("selectedResource");

        CollectionReference resCollection = db.collection("resources");
        resCollection.whereEqualTo("address", resource.getAddress()).whereEqualTo("type", resource.getType()).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        resourceID = document.getId();
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }
}