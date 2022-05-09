package com.albertoornelas.todoappfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.albertoornelas.todoappfirebase.domain.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView linkLogin;
    private EditText txtEmail;
    private EditText txtPassword;
    private Button btnRegister;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        linkLogin = findViewById(R.id.linkLogin);
        btnRegister = findViewById(R.id.btnRegister);
        txtEmail = findViewById(R.id.txtEmailRegister);
        txtPassword = findViewById(R.id.txtPasswordRegister);

        // Create new User (Email and Password)
        btnRegister.setOnClickListener(view -> {
            if (checkFields(txtEmail.getText().toString(), txtPassword.getText().toString())) {
                User user = new User(txtEmail.getText().toString(), txtPassword.getText().toString());

                mAuth.createUserWithEmailAndPassword(
                        user.getEmail(), user.getPassword()
                ).addOnCompleteListener(MainActivity.this, task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(MainActivity.this.getApplicationContext(),
                                "Error de registro " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }else{
                        txtEmail.setText("");
                        txtPassword.setText("");
                        Toast.makeText(
                                MainActivity.this,
                                "Usuario Registrado",
                                Toast.LENGTH_SHORT
                        ).show();
                        FirebaseUser userAuth = mAuth.getCurrentUser();
                        saveUserCollection(userAuth.getUid(), user);
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });

        // Go to LoginActivity
        linkLogin.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

    }


    public void saveUserCollection(String userUid, User user) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("email", user.getEmail());

        System.out.println(user.getEmail());

        db.collection("/users").document(userUid).set(userMap)
                .addOnCompleteListener(task ->
                        Toast.makeText(
                                MainActivity.this,
                                "Usuario gurdado",
                                Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(
                                MainActivity.this,
                                "Error de guardado",
                                Toast.LENGTH_SHORT
                        ).show());
    }

    public boolean checkFields(String email, String password) {
        boolean valid = false;

        if (email.isEmpty()) {
            txtEmail.setError("Ingrea tu correo");
            txtEmail.requestFocus();
        } else if (password.isEmpty()) {
            txtPassword.setError("Ingrea tu contrasena");
            txtPassword.requestFocus();
        } else if ( email.isEmpty() && password.isEmpty()) {
            Toast.makeText(MainActivity.this, "Llena los campos", Toast.LENGTH_SHORT).show();
        } else if (!(email.isEmpty() && password.isEmpty())) {
            valid = true;
        }
        return valid;
    }
}