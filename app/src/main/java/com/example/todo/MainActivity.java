package com.example.todo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.todo.Adapter.ListItemAdapter;
import com.example.todo.Model.TODO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    List<TODO> todoList = new ArrayList<>();
    FirebaseFirestore db;

    RecyclerView listItem;
    RecyclerView.LayoutManager layoutManager;

    FloatingActionButton fab;

    RadioButton rad;

    public MaterialEditText title, note;
    public boolean isUpdate = false; // flag to check is update
    public String idUpdate = ""; // id of item need to update

    ListItemAdapter adapter;

    SpotsDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init Firestore
        db = FirebaseFirestore.getInstance();

        // View
        dialog = new SpotsDialog(this);
        title = (MaterialEditText) findViewById(R.id.title);
        note = (MaterialEditText) findViewById(R.id.note);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        rad = (RadioButton) findViewById(R.id.item_radio);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isUpdate) {
                    setData(title.getText().toString(), note.getText().toString());
                } else {
                    updateData(title.getText().toString(), note.getText().toString());
                    isUpdate = !isUpdate; // reset flag

                    // Hide input keyboard after updated
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        title.setShowSoftInputOnFocus(false);
                    }
                }
                title.requestFocus();
                title.setText("");
                note.setText("");
            }
        });

        listItem = (RecyclerView) findViewById(R.id.recycler_view);
        listItem.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        listItem.setLayoutManager(layoutManager);

        loadData(); // Load data from Firestore
    }

    public void deleteItem(int index) {
        db.collection("TODO").document(todoList.get(index).getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loadData();
                    }
                });
    }

    private void updateData(String title, String note) {
        db.collection("TODO").document(idUpdate)
                .update("title", title, "note", note)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                    }
                });
        // Realtime update refresh data
        db.collection("TODO").document(idUpdate)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        loadData();
                    }
                });
    }

    private void setData(String title, String note) {
        // Add new data
        String id = UUID.randomUUID().toString(); // Random ID
        Map<String, Object> todo = new HashMap<>();
        todo.put("id", id);
        todo.put("title", title);
        todo.put("note", note);

    db.collection("TODO").document(id)
            .set(todo).addOnSuccessListener(new OnSuccessListener<Void>() {
        @Override
        public void onSuccess(Void aVoid) {
            //Refresh data
            loadData();
            Toast.makeText(MainActivity.this, "New task added", Toast.LENGTH_SHORT).show();

        }
    });
    }

    private void loadData() {
        dialog.show();
        todoList.clear(); // Remove old values
        db.collection("TODO")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot doc:task.getResult()) {
                            TODO todo = new TODO(doc.getString("id"),
                                                doc.getString("title"),
                                                doc.getString("note"));
                            todoList.add(todo);
                        }
                        adapter = new ListItemAdapter(MainActivity.this, todoList);
                        listItem.setAdapter(adapter);
                        dialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
