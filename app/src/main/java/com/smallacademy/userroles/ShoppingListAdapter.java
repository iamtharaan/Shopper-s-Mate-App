package com.smallacademy.userroles;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder>
{
    Context context;
    ArrayList<ShoppingListModel> arrayList;
    ShoppingListDatabaseHelper shoppingListDatabaseHelper;
    ShoppingListListener shoppingListListener;
    ShoppingListItemId shoppingListItemIdListner;
    public ShoppingListAdapter(Context context, ArrayList<ShoppingListModel> arrayList, ShoppingListListener shoppingListListener, ShoppingListItemId shoppingListItemIdListner)
    {
        this.shoppingListItemIdListner = shoppingListItemIdListner;
        this.shoppingListListener = shoppingListListener;
        this.context=context;
        this.arrayList=arrayList;
        shoppingListDatabaseHelper =new ShoppingListDatabaseHelper(context);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_list_layout,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position)
    {
        final ShoppingListModel shoppingListModel =arrayList.get(position);
        holder.topic.setText(shoppingListModel.getTopic());
        holder.description.setText(shoppingListModel.getDescription());
        holder.delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
               conformDialog(shoppingListModel.getId());
            }
        });
        holder.update.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                shoppingListItemIdListner.currentItem(shoppingListModel.getId());
            }
        });
    }
    public void conformDialog(final int id)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage("Are you sure you want delete?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which)
            {
                int r= shoppingListDatabaseHelper.deleteItem(id);
                shoppingListListener.response(r);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {

                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public int getItemCount()
    {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView topic,description;
        ImageView delete,update;
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            topic=itemView.findViewById(R.id.topic);
            description=itemView.findViewById(R.id.description);
            delete=itemView.findViewById(R.id.deleteItem);
            update=itemView.findViewById(R.id.edit);

        }
    }
}
