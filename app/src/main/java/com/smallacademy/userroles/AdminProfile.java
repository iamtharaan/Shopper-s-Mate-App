package com.smallacademy.userroles;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class AdminProfile extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private ImageView backButtonAdmin;
    private ImageView profilePictureImageView;
    private TextView fullNameTextView;
    private TextView phoneTextView;
    private TextView emailTextView;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        // Initialize Firebase Authentication and Database references
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        // Get the current user
        currentUser = mAuth.getCurrentUser();

        // Check if the user is logged in
        if (currentUser == null) {
            // If the user is not logged in, redirect to the Login activity and finish this activity
            startActivity(new Intent(AdminProfile.this, Login.class));
            finish();
        } else {
            // User is logged in, continue to retrieve and display user information

            // Initialize views
            backButtonAdmin = findViewById(R.id.BackButtonAdmin);
            profilePictureImageView = findViewById(R.id.profilePicture);
            fullNameTextView = findViewById(R.id.fullName);
            phoneTextView = findViewById(R.id.phoneNumber);
            emailTextView = findViewById(R.id.emailAddress);
            logoutButton = findViewById(R.id.signOut);

            // Set an OnClickListener for the back button
            backButtonAdmin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            // Set an OnClickListener for the logout button
            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Sign out the current user and display a logout message
                    mAuth.signOut();
                    Toast.makeText(AdminProfile.this, "Successfully Logged Out", Toast.LENGTH_SHORT).show();

                    // Navigate to the Login screen and finish the activity
                    startActivity(new Intent(AdminProfile.this, Login.class));
                    finish();
                }
            });

            // Retrieve and display user information
            String userID = currentUser.getUid();
            mDatabase.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // User data exists, retrieve information
                        String fullName = snapshot.child("FullName").getValue(String.class);
                        String phone = snapshot.child("PhoneNumber").getValue(String.class);
                        String email = snapshot.child("UserEmail").getValue(String.class);
                        String profilePictureUrl = snapshot.child("ProfilePictureUrl").getValue(String.class);

                        // Display user information in respective TextViews
                        fullNameTextView.setText(fullName);
                        phoneTextView.setText(phone);
                        emailTextView.setText(email);

                        // Load and display the user's profile picture using Picasso library
                        if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
                            Picasso.get().load(profilePictureUrl).into(profilePictureImageView);
                        } else {
                            // If no profile picture URL is available, set a default profile picture.
                            profilePictureImageView.setImageResource(R.drawable.default_profile_picture);
                        }
                    } else {
                        // User data does not exist in the database
                        Toast.makeText(AdminProfile.this, "User data not found.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FirebaseDatabase", "Error: " + error.getMessage());
                    if (currentUser != null) {
                        // Only show the error toast if the user is still logged in
                        Toast.makeText(AdminProfile.this, "Failed to retrieve user data.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}