package com.example.studentcoursebookingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.accounts.Account;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// todo refactor
public class AccountActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private Button usernameSearchBTN, delBtn, homeBtn;
    private ListView accountListView;
    private ArrayList<QueryDocumentSnapshot> accountList;
    private ArrayAdapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_activity);

        accountList = new ArrayList<>();

        usernameEditText = findViewById(R.id.searchUsername);
        usernameSearchBTN = findViewById(R.id.searchUsernameBtn);
        delBtn = findViewById(R.id.deleteAccount);
        homeBtn = findViewById(R.id.accountActivityHomeBtn);
        accountListView = findViewById(R.id.accountListView);

        usernameSearchBTN.setOnClickListener(search);
        delBtn.setOnClickListener(deleteUser);
        homeBtn.setOnClickListener(openHomePageActivity);
    }

    private View.OnClickListener openHomePageActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(AccountActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    };

    private void viewList() {
        HashMap<String, Object> account = (HashMap) accountList.get(0).getData();
        String role = (String) account.get("role");
        String name = (String) account.get("name");

        String accountInfo = "username: " + name + " role: " + role;
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Collections.singletonList(accountInfo));
        accountListView.setAdapter(adapter);
    }

    private void viewListEmpty() {
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Collections.singletonList("Nothing found"));
        accountListView.setAdapter(adapter);
    }

    private View.OnClickListener deleteUser = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!accountList.isEmpty()) {
                HashMap<String, Object> account = (HashMap) accountList.get(0).getData();
                String role = (String) account.get("role");
                String name = (String) account.get("name");

                CollectionReference dbUser = db.collection("users");
                Map<String,Object> updates = new HashMap<>();
                updates.put(accountList.get(0).getId(), FieldValue.delete());
                //Toast.makeText(AccountActivity.this, accountList.get(0).getId(), Toast.LENGTH_LONG).show();
//                dbUser.document().update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(AccountActivity.this, "Success!", Toast.LENGTH_LONG).show();
//                        } else {
//                            Toast.makeText(AccountActivity.this, "Failed!", Toast.LENGTH_LONG).show();
//                        }
//
//                    }
//                });

                dbUser.document(accountList.get(0).getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        Toast.makeText(AccountActivity.this, "Success!", Toast.LENGTH_LONG).show();

                    }
                });
            }
        }
    };

    private View.OnClickListener search = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            accountList.clear();
            String username = usernameEditText.getText().toString().trim();

            CollectionReference dbUser = db.collection("users");

            dbUser.whereEqualTo("name", username).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        //Toast.makeText(AccountActivity.this, "Noth", Toast.LENGTH_LONG).show();
                        Toast.makeText(AccountActivity.this, Integer.toString(task.getResult().size()), Toast.LENGTH_LONG).show();

                        for (QueryDocumentSnapshot document: task.getResult()) {
                            Toast.makeText(AccountActivity.this, document.getData().toString() , Toast.LENGTH_SHORT).show();
                            Log.d("SUCCESS", document.getId() + " => " + document.getData());
                            Map<String, Object> user = document.getData();
                            accountList.add(document);
                        }
                        // todo improve logic here
                        if (!accountList.isEmpty()) {
                            viewList();
                        } else {
                            viewListEmpty();
                        }
                    }
                }
            });
        }
    };
}


