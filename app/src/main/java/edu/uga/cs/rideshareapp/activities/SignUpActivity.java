package edu.uga.cs.rideshareapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import edu.uga.cs.rideshareapp.R;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailEditText, passwordEditText;
    private Button signupButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.signUpMail);
        passwordEditText = findViewById(R.id.signUpPassword);
        signupButton = findViewById(R.id.signUpMailButton);

        signupButton.setOnClickListener(view -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            // ✅ VALIDATION must happen BEFORE calling Firebase
            if (!isValidUGAEmail(email)) {
                Toast.makeText(this, "Please enter a valid @uga.edu email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidPassword(password)) {
                Toast.makeText(this, "Password must be at least 8 characters, include uppercase, lowercase, number, and symbol", Toast.LENGTH_LONG).show();
                return;
            }

            // ✅ Only call Firebase if validation passes
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Sign-up successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpActivity.this, SelectionActivity.class));
                            finish();
                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        });
    }

    // ✅ UGA Email check
    private boolean isValidUGAEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches() && email.endsWith("@uga.edu");
    }

    // ✅ Strong password check
    private boolean isValidPassword(String password) {
        return password.length() >= 8 &&
                password.matches(".*[A-Z].*") &&       // at least one uppercase
                password.matches(".*[a-z].*") &&       // at least one lowercase
                password.matches(".*\\d.*") &&         // at least one digit
                password.matches(".*[!@#$%^&*()].*");  // at least one symbol
    }
}