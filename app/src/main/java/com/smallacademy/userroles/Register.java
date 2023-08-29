package com.smallacademy.userroles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    EditText fullName, email, password, phone;
    Button registerBtn, goToLogin;
    FirebaseAuth fAuth;
    FirebaseDatabase fDatabase;
    DatabaseReference usersRef;
    CheckBox isAdminBox, isUserBox, showPasswordCheckbox;
    boolean isRegistrationInProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance();
        usersRef = fDatabase.getReference("Users");

        fullName = findViewById(R.id.registerName);
        email = findViewById(R.id.registerEmail);
        password = findViewById(R.id.registerPassword);
        phone = findViewById(R.id.registerPhone);
        registerBtn = findViewById(R.id.registerBtn);
        goToLogin = findViewById(R.id.gotoLogin);
        isAdminBox = findViewById(R.id.isAdmin);
        isUserBox = findViewById(R.id.isUser);
        showPasswordCheckbox = findViewById(R.id.showPasswordCheckbox);

        // Checkbox logic
        isUserBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    isAdminBox.setChecked(false);
                }
            }
        });

        isAdminBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    isUserBox.setChecked(false);
                }
            }
        });

        // Set up show/hide password toggle
        showPasswordCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    // Show the password
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    // Hide the password
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if the registration process is already in progress
                if (isRegistrationInProgress) {
                    return; // Do nothing if registration is already in progress
                }

                // Validate each field
                boolean isFullNameValid = isFullNameValid();
                boolean isEmailValid = isEmailValid();
                boolean isPhoneValid = isPhoneValid();
                boolean isPasswordValid = checkField(password, true);
                boolean isUserChecked = isUserBox.isChecked();

                // Check if the isUser checkbox is checked
                if (!isUserChecked) {
                    // Show a toast message if the isUser checkbox is not checked
                    Toast.makeText(Register.this, "Agree to the Privacy Policy", Toast.LENGTH_SHORT).show();
                    return; // Exit the onClick method without proceeding further
                }

                // Check if all fields are valid
                if (isFullNameValid && isEmailValid && isPhoneValid && isPasswordValid) {
                    // Start the user registration process
                    String emailInput = email.getText().toString().trim();
                    String passwordInput = password.getText().toString().trim();
                    String fullNameInput = fullName.getText().toString().trim();
                    String phoneInput = phone.getText().toString().trim();

                    // Disable the button to prevent multiple clicks during registration
                    registerBtn.setEnabled(false);
                    isRegistrationInProgress = true;

                    fAuth.createUserWithEmailAndPassword(emailInput, passwordInput).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseUser user = fAuth.getCurrentUser();
                            Toast.makeText(Register.this, "Account Created", Toast.LENGTH_SHORT).show();

                            DatabaseReference userRef = usersRef.child(user.getUid());
                            Map<String, Object> userInfo = new HashMap<>();
                            userInfo.put("FullName", fullNameInput);
                            userInfo.put("UserEmail", emailInput);
                            userInfo.put("PhoneNumber", phoneInput);

                            // Specify if the user is admin
                            if (isAdminBox.isChecked()) {
                                userInfo.put("isAdmin", "1");
                            }
                            if (isUserBox.isChecked()) {
                                userInfo.put("isUser", "1");
                            }

                            userRef.setValue(userInfo);

                            if (isAdminBox.isChecked()) {
                                startActivity(new Intent(getApplicationContext(), AdminPanel.class));
                            } else if (isUserBox.isChecked()) {
                                startActivity(new Intent(getApplicationContext(), SplashUserActivity.class));
                            }
                            finish();

                            // Enable the button and reset the registration in progress flag
                            registerBtn.setEnabled(true);
                            isRegistrationInProgress = false;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Register.this, "Failed to Create Account: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            // Enable the button and reset the registration in progress flag
                            registerBtn.setEnabled(true);
                            isRegistrationInProgress = false;
                        }
                    });
                }
            }
        });

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });
    }

    public boolean checkField(EditText textField, boolean isPassword) {
        String fieldValue = textField.getText().toString().trim();

        if (fieldValue.isEmpty()) {
            if (isPassword) {
                textField.setError("Password must have at least 8 characters; contain at least 1 uppercase, lowercase, special character, and number each");
            } else {
                textField.setError("This field is required");
            }
            return false;
        } else if (isPassword) {
            String passwordValidationMessage = isValidPassword(fieldValue);
            if (passwordValidationMessage != null) {
                textField.setError(passwordValidationMessage);
                return false;
            }
        }

        return true;
    }

    private boolean isFullNameValid() {
        String fullNameInput = fullName.getText().toString().trim();
        if (fullNameInput.isEmpty()) {
            fullName.setError("This field is required");
            return false;
        } else if (fullNameInput.length() < 5) {
            fullName.setError("Username must be at least 5 characters long");
            return false;
        }
        return true;
    }

    private boolean isEmailValid() {
        String emailInput = email.getText().toString().trim();
        if (emailInput.isEmpty()) {
            email.setError("This field is required");
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailInput).matches() ||
                (!emailInput.endsWith("@gmail.com") && !emailInput.endsWith("@yahoo.com"))) {
            email.setError("Enter a valid email address");
            return false;
        }
        return true;
    }

    private boolean isPhoneValid() {
        String phoneInput = phone.getText().toString().trim();
        if (phoneInput.isEmpty()) {
            phone.setError("This field is required");
            return false;
        } else if (!phoneInput.startsWith("+60") && !phoneInput.startsWith("60")) {
            phone.setError("Phone number must start with +60/60");
            return false;
        } else if (phoneInput.length() < 9) {
            phone.setError("Phone number must have at least 9 digits");
            return false;
        }
        return true;
    }

    public String isValidPassword(String password) {
        // Check for at least 8 characters
        if (password.length() < 8) {
            return "Password must have at least 8 characters";
        }

        // Check for at least one uppercase letter
        if (!password.matches(".*[A-Z].*")) {
            return "Password must contain at least one uppercase letter";
        }

        // Check for at least one lowercase letter
        if (!password.matches(".*[a-z].*")) {
            return "Password must contain at least one lowercase letter";
        }

        // Check for at least one special character
        if (!password.matches(".*[!@#$%^&*()\\-_+=|\\\\{}\\[\\]:\";<>,.?/~`].*")) {
            return "Password must contain at least one special character !@#$%^&*()\\-_+=|\\\\{}\\[\\]:\";<>,.?/~`";
        }

        // Check for at least one number
        if (!password.matches(".*\\d.*")) {
            return "Password must contain at least one number";
        }

        // If all criteria are met, return null (indicating no error)
        return null;
    }
}