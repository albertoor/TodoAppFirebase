package com.albertoornelas.todoappfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.albertoornelas.todoappfirebase.domain.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button btnLogin;
    private EditText txtEmail;
    private EditText txtPassword;
    private TextView linkRegister;
    private FirebaseAuth.AuthStateListener authStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        txtEmail = findViewById(R.id.txtEmailLogin);
        txtPassword = findViewById(R.id.txtPasswordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        linkRegister = findViewById(R.id.linkRegister);

        authStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                Toast.makeText(LoginActivity.this, "Usuario logeado", Toast.LENGTH_SHORT).show();
                Intent I = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(I);
            } else {
                Toast.makeText(LoginActivity.this, "Login to continue", Toast.LENGTH_SHORT).show();
            }
        };

        linkRegister.setOnClickListener(view ->{
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(view -> {
            if (checkFields(txtEmail.getText().toString(), txtPassword.getText().toString())) {
                User user = new User(txtEmail.getText().toString(), txtPassword.getText().toString());
                mAuth.signInWithEmailAndPassword(
                        user.getEmail(), user.getPassword()
                ).addOnCompleteListener(
                        LoginActivity.this, task -> {
                            if (!task.isSuccessful()) {
                                Toast.makeText(
                                        LoginActivity.this,
                                        "Credenciales no validas",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }else{
                                FirebaseUser userFU = mAuth.getCurrentUser();
                                Toast.makeText(
                                        LoginActivity.this,
                                        String.format("Bienvenido usuario:%s", userFU.getEmail()),
                                        Toast.LENGTH_SHORT
                                ).show();
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                            }
                        }
                );
            }
        });
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
            Toast.makeText(LoginActivity.this, "Llena los campos", Toast.LENGTH_SHORT).show();
        } else if (!(email.isEmpty() && password.isEmpty())) {
            valid = true;
        }
        return valid;
    }

    @Override
    protected void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }
}