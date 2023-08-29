package com.smallacademy.userroles;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

public class ScannerFragment extends Fragment {

    private DecoratedBarcodeView barcodeScannerView;
    private boolean isScanning = true;
    private boolean isAlertDialogShowing = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scanner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        barcodeScannerView = view.findViewById(R.id.barcodeScannerView);

        barcodeScannerView.setStatusText("Scan a barcode");
        barcodeScannerView.decodeContinuous(callback -> {
            if (isScanning) {
                if (callback.getText() != null) {
                    isScanning = false; // Stop scanning when a barcode is detected
                    String scannedBarcode = callback.getText();
                    // Fetch product details from Firebase based on the scanned barcode
                    fetchProductDetails(scannedBarcode);
                } else {
                    // Handle scan cancellation or errors
                    showAlertDialog("Scan Error", "Failed to scan the barcode.");
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        barcodeScannerView.resume();

        // Ensure scanning is resumed only if the alert dialog is not showing
        if (!isAlertDialogShowing) {
            isScanning = true;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        barcodeScannerView.pause();
        isScanning = false; // Stop scanning when the fragment is paused
    }

    private void fetchProductDetails(String barcode) {
        Query query = FirebaseDatabase.getInstance().getReference().child("products").orderByChild("barcode").equalTo(barcode);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                        ProductScanModel product = productSnapshot.getValue(ProductScanModel.class);
                        Intent intent = new Intent(requireContext(), ScanItem.class);
                        intent.putExtra("product", product);
                        startActivity(intent);
                        return; // Exit the loop after finding a match
                    }
                }

                showAlertDialog("Barcode Not Found", "The scanned barcode does not exist in the database.");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showAlertDialog("Error", "Failed to fetch product details from the database.");
            }
        });
    }

    private void showAlertDialog(String title, String message) {
        isScanning = false; // Stop scanning when the alert dialog shows up
        isAlertDialogShowing = true; // Set the flag to indicate that the alert dialog is showing

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                isAlertDialogShowing = false; // Reset the flag when the alert dialog is dismissed
                restartBarcodeScanning();
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                isAlertDialogShowing = false; // Reset the flag when the alert dialog is dismissed without clicking "OK"
                restartBarcodeScanning();
            }
        });
        builder.show();
    }

    private void restartBarcodeScanning() {
        if (!isAlertDialogShowing) {
            isScanning = true; // Resume scanning only if the alert dialog is not showing
            barcodeScannerView.resume();
        }
    }
}