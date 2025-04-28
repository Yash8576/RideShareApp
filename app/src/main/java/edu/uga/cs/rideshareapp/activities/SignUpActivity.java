package edu.uga.cs.rideshareapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

import edu.uga.cs.rideshareapp.R;
import edu.uga.cs.rideshareapp.adapters.CoinsManager;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailEditText, passwordEditText;
    private Button signUpButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.sign_up_scroll), (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(0, 0, 0, 0);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.signUpMail);
        passwordEditText = findViewById(R.id.signUpPassword);
        signUpButton = findViewById(R.id.signUpMailButton);

        signUpButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (!isValidEmail(email)) {
                Toast.makeText(SignUpActivity.this, "Enter a valid email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isStrongPassword(password)) {
                Toast.makeText(SignUpActivity.this, "Password must have 6+ characters, including uppercase, lowercase, number, and symbol", Toast.LENGTH_LONG).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            CoinsManager.initializeCoinsIfNeeded(SignUpActivity.this);
                            Toast.makeText(SignUpActivity.this, "Sign up successful! 50 coins awarded!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpActivity.this, SelectionActivity.class));
                            finish();
                        } else {
                            Toast.makeText(SignUpActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isStrongPassword(String password) {
        // ✅ At least 6 characters, 1 uppercase, 1 lowercase, 1 digit, 1 special char
        Pattern PASSWORD_PATTERN = Pattern.compile(
                "^" +
                        "(?=.*[0-9])" +         // at least one digit
                        "(?=.*[a-z])" +         // at least one lowercase letter
                        "(?=.*[A-Z])" +         // at least one uppercase letter
                        "(?=.*[@#$%^&+=!])" +   // at least one special character
                        ".{6,}" +               // at least 6 characters
                        "$"
        );
        return PASSWORD_PATTERN.matcher(password).matches();
    }
}