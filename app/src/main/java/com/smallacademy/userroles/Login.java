package com.smallacademy.userroles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    private EditText email, password;
    private Button loginBtn, gotoRegister;
    private CheckBox showPasswordCheckbox;
    private FirebaseAuth fAuth;
    private FirebaseDatabase fDatabase;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance();
        usersRef = fDatabase.getReference("Users");

        email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);
        loginBtn = findViewById(R.id.loginBtn);
        gotoRegister = findViewById(R.id.gotoRegister);
        showPasswordCheckbox = findViewById(R.id.showPasswordCheckbox);

        showPasswordCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                int passwordInputType = isChecked ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
                password.setInputType(passwordInputType);
                password.setSelection(password.getText().length());
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean valid = validateInputFields();

                if (!valid) {
                    return; // There are validation errors, do not attempt to sign in
                }

                String emailText = email.getText().toString().trim();
                String passwordText = password.getText().toString().trim();

                fAuth.signInWithEmailAndPassword(emailText, passwordText)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Toast.makeText(Login.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                                checkUserAccessLevel(authResult.getUser().getUid());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                email.setError("Invalid email address");
                                Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        gotoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });
    }

    private boolean validateInputFields() {
        boolean valid = true;
        String emailText = email.getText().toString().trim();
        String passwordText = password.getText().toString().trim();

        if (emailText.isEmpty()) {
            email.setError("This field is required");
            valid = false;
        } else if (!isValidEmail(emailText)) {
            email.setError("Invalid email address");
            valid = false;
        } else if (!emailText.endsWith("@gmail.com") && !emailText.endsWith("@yahoo.com")) {
            email.setError("Email must end with @gmail.com or @yahoo.com");
            valid = false;
        } else {
            email.setError(null);
        }

        String passwordErrorMessage = checkPassword(passwordText);
        if (passwordErrorMessage != null) {
            password.setError(passwordErrorMessage);
            valid = false;
        } else {
            password.setError(null);
        }

        return valid;
    }

    private void checkUserAccessLevel(String uid) {
        DatabaseReference userRef = usersRef.child(uid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("isAdmin").exists()) {
                    startActivity(new Intent(Login.this, SplashAdminActivity.class));
                    finish();
                } else if (snapshot.child("isUser").exists()) {
                    startActivity(new Intent(Login.this, SplashUserActivity.class));
                    finish();
                } else {
                    Toast.makeText(Login.this, "Invalid access level", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Login.this, "Error checking user access level", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String checkPassword(String password) {
        if (password.isEmpty()) {
            return "Password must have at least 8 characters; contain at least 1 uppercase, lowercase, special character, and number each";
        } else if (password.length() < 8) {
            return "Password must have at least 8 characters";
        } else if (!password.matches(".*\\d.*")) {
            return "Password must contain at least one number";
        } else if (!password.matches(".*[A-Z].*")) {
            return "Password must contain at least one uppercase letter";
        } else if (!password.matches(".*[a-z].*")) {
            return "Password must contain at least one lowercase letter";
        } else if (!password.matches(".*[!@#$%^&*()\\-_+=|\\\\{}\\[\\]:\";<>,.?/~`].*")) {
            return "Password must contain at least one special character !@#$%^&*()\\-_+=|\\\\{}\\[\\]:\";<>,.?/~`";
        } else {
            return null; // Password is valid, return null for no error message
        }
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            DatabaseReference userRef = usersRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child("isAdmin").exists()) {
                        startActivity(new Intent(getApplicationContext(), AdminPanel.class));
                        finish();
                    } else if (snapshot.child("isUser").exists()) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    } else {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getApplicationContext(), Login.class));
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Login.this, "Error checking user access level", Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getApplicationContext(), Login.class));
                    finish();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}