package com.smallacademy.userroles;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class AccountFragment extends Fragment {

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    private Button logout;
    private ImageView profilePictureImageView;
    private TextView greetingTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        logout = view.findViewById(R.id.signOut);
        profilePictureImageView = view.findViewById(R.id.profilePicture);
        greetingTextView = view.findViewById(R.id.greeting);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getContext(), "Successfully Logged Out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getContext(), Login.class));
                getActivity().finish();
            }
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        final TextView fullNameTextView = view.findViewById(R.id.fullName);
        final TextView phoneTextView = view.findViewById(R.id.phoneNumber);
        final TextView emailTextView = view.findViewById(R.id.emailAddress);

        DatabaseReference userRef = reference.child(userID);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String fullName = snapshot.child("FullName").getValue(String.class);
                    String phone = snapshot.child("PhoneNumber").getValue(String.class);
                    String email = snapshot.child("UserEmail").getValue(String.class);
                    String profilePictureUrl = snapshot.child("ProfilePictureUrl").getValue(String.class);

                    fullNameTextView.setText(fullName);
                    phoneTextView.setText(phone);
                    emailTextView.setText(email);

                    if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
                        Picasso.get().load(profilePictureUrl).into(profilePictureImageView);
                    } else {
                        // If no profile picture URL is available, you can set a default profile picture.
                        profilePictureImageView.setImageResource(R.drawable.default_profile_picture);
                    }

                    // Update the greeting text with the account name
                    String greetingText = "Welcome, " + fullName + "!";
                    greetingTextView.setText(greetingText);
                } else {
                    Toast.makeText(getContext(), "User data not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Check the error type and display appropriate messages.
                if (error.getCode() == DatabaseError.PERMISSION_DENIED) {
                    Toast.makeText(getContext(), "Access to user data is denied.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to retrieve user data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}