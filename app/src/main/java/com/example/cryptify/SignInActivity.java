package com.example.cryptify;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignInActivity extends AppCompatActivity {
    private EditText usernameInput, passwordInput;
    private Button btnSignIn;
    private TextView toggleLink;
    private boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);

        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        btnSignIn = findViewById(R.id.btnAction);
        toggleLink = findViewById(R.id.toggleLink);

        btnSignIn.setOnClickListener(view -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(SignInActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Simulate successful login
            Toast.makeText(SignInActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SignInActivity.this, IntroActivity.class));
            finish();
        });

        toggleLink.setOnClickListener(view -> {
            startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            finish();
        });

        passwordInput.setOnClickListener(v -> {
            int selection = passwordInput.getSelectionEnd();
            if (passwordVisible) {
                passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordVisible = false;
            } else {
                passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                passwordVisible = true;
            }
            passwordInput.setSelection(selection);
        });
    }
}
