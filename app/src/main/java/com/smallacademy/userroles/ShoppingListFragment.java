package com.smallacademy.userroles;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ShoppingListFragment extends Fragment implements ShoppingListListener, ShoppingListItemId {
    FloatingActionButton floatButton;
    RecyclerView recyclerView;
    ShoppingListDatabaseHelper shoppingListDatabaseHelper;
    AlertDialog dialog;
    RecyclerView.Adapter adapter;
    int currentItemId;
    ArrayList<ShoppingListModel> shoppingListModelArrayList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        shoppingListDatabaseHelper = new ShoppingListDatabaseHelper(getActivity());
        floatButton = view.findViewById(R.id.fab);
        recyclerView = view.findViewById(R.id.todolistRecycler);
        shoppingListModelArrayList = displayAllItems();
        displayItemsInRecyclerView(shoppingListModelArrayList);

        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialogToAddItem();
            }
        });

        return view;
    }


    // Method for RecyclerView
    public void displayItemsInRecyclerView(ArrayList<ShoppingListModel> arrayList) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        adapter = new ShoppingListAdapter(getActivity(), arrayList, this, this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    // This alert dialog is to add a new item to the database
    public void showAlertDialogToAddItem() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View view1 = LayoutInflater.from(getActivity()).inflate(R.layout.alert_dialog_layout, null);
        builder.setView(view1);
        Button addButton = view1.findViewById(R.id.addButton);
        final EditText topic = view1.findViewById(R.id.itemName);
        final EditText description = view1.findViewById(R.id.description);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String itemName = topic.getText().toString();
                String itemDescription = description.getText().toString();
                if (itemName.isEmpty()) {
                    topic.setError("Required");
                    topic.requestFocus();
                    return;
                }
                if (itemDescription.isEmpty()) {
                    description.setError("Required");
                    description.requestFocus();
                    return;
                }
                addItemToDatabase(itemName, itemDescription);
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    // This method is to add an item to the database
    public void addItemToDatabase(String topic, String description) {
        boolean res = shoppingListDatabaseHelper.addItemToList(topic, description);
        if (res) {
            Toast.makeText(getActivity(), "Item Added successfully", Toast.LENGTH_SHORT).show();
            dialog.cancel();
            reloadListItem();
        } else {
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    // This method is to retrieve all data from the database
    public ArrayList<ShoppingListModel> displayAllItems() {
        ArrayList<ShoppingListModel> shoppingListModelArrayList = shoppingListDatabaseHelper.getAllItems();
        return shoppingListModelArrayList;
    }

    // This method is to reload all list items in the RecyclerView
    public void reloadListItem() {
        displayItemsInRecyclerView(displayAllItems());
    }

    // This is an interface method which is responsible for refreshing the list whenever any data item is deleted from the list.
    // This method will be triggered whenever we delete any item from the RecyclerView adapter
    @Override
    public void response(int i) {
        reloadListItem();
    }

    // This method is to show the alert dialog to update the item in the list
    public void showAlertDialogToUpdateItem(ShoppingListModel shoppingListModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View view1 = LayoutInflater.from(getActivity()).inflate(R.layout.alert_dialog_update_layout, null);
        builder.setView(view1);
        Button updateButton = view1.findViewById(R.id.updateButton);
        final EditText topic = view1.findViewById(R.id.itemName);
        final EditText description = view1.findViewById(R.id.description);
        topic.setText(shoppingListModel.getTopic());
        description.setText(shoppingListModel.getDescription());
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String itemName = topic.getText().toString();
                String itemDescription = description.getText().toString();
                if (itemName.isEmpty()) {
                    topic.setError("Required");
                    topic.requestFocus();
                    return;
                }
                if (itemDescription.isEmpty()) {
                    description.setError("Required");
                    description.requestFocus();
                    return;
                }
                updateCurrentItem(currentItemId, itemName, itemDescription);
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    // This interface method is triggered whenever any data item wants to be updated from the adapter.
    // It retrieves the current list item ID, which will be used to retrieve the information about the particular item from the database
    @Override
    public void currentItem(int id) {
        currentItemId = id;
        ShoppingListModel shoppingListModel = shoppingListDatabaseHelper.getListItems(id);
        // Here we are retrieving a single item from the database
        showAlertDialogToUpdateItem(shoppingListModel);
    }

    // This method is to update the data
    public void updateCurrentItem(int id, String topic, String description) {
        boolean response = shoppingListDatabaseHelper.updateItem(id, topic, description);
        if (response) {
            Toast.makeText(getActivity(), "Updated successfully", Toast.LENGTH_SHORT).show();
            dialog.cancel();
            reloadListItem();
        }
    }
}
