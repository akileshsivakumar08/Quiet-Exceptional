package com.example.allowrepeatcallers;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.quietexceptional.R;

import java.util.ArrayList;

public class PersonAdapter extends ArrayAdapter<class_Buddy> {
    private Context mcontext;
    private int mResource;

    public PersonAdapter(@NonNull Context context, int resource, @NonNull ArrayList<class_Buddy> objects){
        super(context,resource, objects);
        this.mcontext=context;
        this.mResource=resource;
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater=LayoutInflater.from(mcontext);
        convertView= layoutInflater.inflate(mResource,parent,false);
        if(getItem(position)!=null) {
            TextView txtName = convertView.findViewById(R.id.txtName);
            TextView txtPhno = convertView.findViewById(R.id.txtPhNo);
            txtName.setText(getItem(position).getBuddy_name());
            txtPhno.setText(getItem(position).getBuddy_PhNo());
            if(getItem(position).isFavourite()) {
                ImageView favouriteheart = convertView.findViewById(R.id.favouriteheart);
                favouriteheart.setColorFilter(Color.parseColor("#F2F1E8"));
            }
        }
        return convertView;
    }

}
