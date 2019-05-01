package com.example.recipeapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Registration extends AppCompatActivity {

    ImageView back;
    Button registerButton;
    EditText re_email, re_password, re_confirm;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);

        back = findViewById(R.id.registration_back);
        registerButton = findViewById(R.id.register);
        re_email = findViewById(R.id.register_email);
        re_password = findViewById(R.id.register_password);
        re_confirm = findViewById(R.id.register_confirm_password);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Registration.this, Login.class);
                startActivity(i);
                finish();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    private void register() {

        String email = re_email.getText().toString().trim();
        String password = re_password.getText().toString().trim();
        String confirm = re_confirm.getText().toString().trim();

        if (email.isEmpty()) {
            re_email.setError("Email is required");
            re_email.requestFocus();
            return;
        }

        if (!isValidEmail(email)) {
            re_email.setError("Email format is wrong");
            re_email.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            re_password.setError("Password is required");
            re_password.requestFocus();
            return;
        }

        if (password.length() < 6) {
            re_password.setError("Password minimum 6 characters");
            re_password.requestFocus();
            return;
        }

        if (confirm.isEmpty()) {
            re_confirm.setError("Confirm is required");
            re_confirm.requestFocus();
            return;
        }

        if (confirm.equals(password)) {
            mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Intent i = new Intent(Registration.this, Setting.class);
                        startActivity(i);
                        finish();
                    }
                }
            });
        } else {
            re_confirm.setError("Confirm is wrong");
            re_confirm.requestFocus();
            return;
        }

    }

    private boolean isValidEmail(String target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

}
