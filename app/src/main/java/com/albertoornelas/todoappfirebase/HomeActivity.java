package com.albertoornelas.todoappfirebase;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.albertoornelas.todoappfirebase.adapter.TodoAdapter;
import com.albertoornelas.todoappfirebase.domain.Todo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView currentTxtEmail;
    private Button btnLogout;
    private Button btnAddTodo;
    private EditText txtTitleTodo;
    private EditText txtDescriptionTodo;
    private FirebaseFirestore db;

    private List<Todo> todoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentTxtEmail = findViewById(R.id.txtCurrentEmail);
        btnLogout = findViewById(R.id.btnLogout);
        btnAddTodo = findViewById(R.id.btnAddTodo);

        txtTitleTodo = findViewById(R.id.txtTitleTodo);
        txtDescriptionTodo = findViewById(R.id.txtDescriptionTodo);

        currentTxtEmail.setText(String.format("Hola \n %s", mAuth.getCurrentUser().getEmail()));

        fetchTodos();

        btnLogout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
        });

        btnAddTodo.setOnClickListener(view -> {
            if (checkFields(txtTitleTodo.getText().toString(), txtDescriptionTodo.getText().toString())) {
                Todo todo = new Todo(txtTitleTodo.getText().toString(), txtDescriptionTodo.getText().toString());
                Map<String, Object> todoData = new HashMap<>();
                todoData.put("title", todo.getTitleTodo());
                todoData.put("description", todo.getDescriptionTodo());

                db.collection("users").document(mAuth.getUid())
                        .collection("todos")
                        .document()
                        .set(todoData)
                        .addOnSuccessListener(aVoid -> {
                            txtTitleTodo.setText("");
                            txtDescriptionTodo.setText("");
                            Toast.makeText(HomeActivity.this,
                                    String.format("Todo %s creado", todo.getTitleTodo()),
                                    Toast.LENGTH_SHORT).show();
                            todoList.clear();
                            fetchTodos();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(HomeActivity.this,
                                    String.format("El todo no se pudo crear"),
                                    Toast.LENGTH_SHORT).show();
                        });
            }
        });

    }

    public void fetchTodos() {
        todoList = new ArrayList<>();
        db.collection("users").document(mAuth.getCurrentUser().getUid()).
                collection("todos").get()
                .addOnCompleteListener(task -> {
                        for (DocumentSnapshot snapshot : task.getResult()) {
                            Todo todo = new Todo(
                                    snapshot.getId(),
                                    snapshot.getString("title"),
                                    snapshot.getString("description")
                            );
                            todoList.add(todo);
                        }
                        TodoAdapter todoAdapter = new TodoAdapter(HomeActivity.this, todoList, this);
                        RecyclerView recyclerView = findViewById(R.id.todosListRecView);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(this));
                        recyclerView.setAdapter(todoAdapter);
                }).addOnFailureListener(e -> {
                    Toast.makeText(HomeActivity.this, "Algo salio mal", Toast.LENGTH_SHORT).show();
        });
    }

    public boolean checkFields(String titleTodo, String descTodo) {
        boolean valid = false;

        if (titleTodo.isEmpty()) {
            txtTitleTodo.setError("Ingresa un titulo");
            txtTitleTodo.requestFocus();
        } else if (descTodo.isEmpty()) {
            txtDescriptionTodo.setError("Ingrea una descripcion");
            txtDescriptionTodo.requestFocus();
        } else if ( titleTodo.isEmpty() && descTodo.isEmpty()) {
            Toast.makeText(HomeActivity.this, "Llena los campos", Toast.LENGTH_SHORT).show();
        } else if (!(titleTodo.isEmpty() && descTodo.isEmpty())) {
            valid = true;
        }
        return valid;
    }
}