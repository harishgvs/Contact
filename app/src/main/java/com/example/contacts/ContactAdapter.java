package com.example.contacts;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;


import com.example.contacts.databinding.ItemContactAdapterBinding;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;


public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.UserViewHolder> {
    private List<Contact> listContacts;
    private Context mContext;


    public ContactAdapter(Context context, List<Contact> listContacts) {
        this.mContext = context;
        this.listContacts = listContacts;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UserViewHolder(ItemContactAdapterBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));

    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        Contact contact = listContacts.get(position);

        File file = new File(contact.getImage());

        holder.itemContactAdapterBinding.tvName.setText(listContacts.get(position).getName());
        holder.itemContactAdapterBinding.tvEmail.setText(listContacts.get(position).getEmail());
        holder.itemContactAdapterBinding.tvNumber.setText(listContacts.get(position).getNumber());
        Picasso.get().load(file). placeholder(R.drawable.ph).into(holder.itemContactAdapterBinding.ivProfile);

        holder.itemContactAdapterBinding.ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, AddContactActivity.class);
                intent.putExtra("name", listContacts.get(position).getName());
                intent.putExtra("email", listContacts.get(position).getEmail());
                intent.putExtra("number", listContacts.get(position).getNumber());
                intent.putExtra("image", listContacts.get(position).getImage());
                intent.putExtra("id", listContacts.get(position).getId());
                mContext.startActivity(intent);

                Constants.EDIT_CLICKED = false;

            }
        });

        holder.itemContactAdapterBinding.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listContacts.remove(position);
                notifyItemRemoved(position);
                holder.databaseHelper.deleteContact(contact);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        int v = Log.v(ContactAdapter.class.getSimpleName(), "" + listContacts.size());
        return listContacts.size();
    }


    public class UserViewHolder extends RecyclerView.ViewHolder {
        ItemContactAdapterBinding itemContactAdapterBinding;//Name of the test_list_item.xml in camel case + "Binding"
        DatabaseHelper databaseHelper;

        public UserViewHolder(ItemContactAdapterBinding binding) {
            super(binding.getRoot());
            itemContactAdapterBinding = binding;
            databaseHelper = new DatabaseHelper(mContext);
        }
    }
}